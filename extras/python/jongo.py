#!/usr/bin/env python
#
# Copyright (C) 2011 Alejandro Ayuso
#
# This file is part of Jongo
#
# Jongo is free software: you can redistribute
# it and/or modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# Jongo is distributed in the hope that it will
# be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
# of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with the Monocaffe Connection Manager. If not, see
# <http://www.gnu.org/licenses/>.
#

# An API to abstract the use of jongo from a Python application. It uses
# pretty standard libraries in Python 2.7 so it should work with any
# version of Python from 2.6 to 2.7
# With Python 2.4 you'll need to get the simplejson library 
# http://pypi.python.org/pypi/simplejson/2.1.0

try:
    import json
except ImportError:
    import simplejson as json
import httplib, urllib


class JongoEvent(object):
    def __init__(self):
        self.handlers = set()

    def handle(self, handler):
        self.handlers.add(handler)
        return self

    def unhandle(self, handler):
        try:
            self.handlers.remove(handler)
        except:
            raise ValueError("Handler is not handling this event, so cannot remove it.")
        return self

    def fire(self, *args, **kargs):
        for handler in self.handlers:
            handler(*args, **kargs)

    def getHandlerCount(self):
        return len(self.handlers)

    __iadd__ = handle
    __isub__ = unhandle
    __call__ = fire
    __len__ = getHandlerCount

class Response(object):
    def __init__(self):
        self.raw = ""
        self.success = False
        self.count = 0
        self.code = 0 
        self.data = []
        self.message = ""

    def __str__(self):
        return '{response}'

    def callback(self, response):
        self.code = response.status
        if self.code == 200 or self.code == 201:
            self.process_success(response)
        else:
            self.process_failure(response)

    def process_success(self, response):
        self.success = True
        self.raw = json.loads(response.read())
        self.count = response.getheader('Content-Count')
        for i in self.raw['cells']:
            self.data.append(i)

    def process_failure(self, response):
        self.success = False
        self.raw = json.loads(response.read())
        self.message = self.raw['message']

class Request(object):
    def __init__(self, url, path, method='GET', params={}):
        self.url = url
        self.path = path
        self.method = method
        self.headers = {"Content-type": "application/json", "Accept": "application/json"}
        self.response = Response()
        if type(params) is str:
            self.params = params
        else:
            self.params = json.dumps(params)

    def perform(self):
        conn = httplib.HTTPConnection(self.url)
        # print "%s\t%s\t\t\t\t %s" % (self.method, self.path, self.params)
        conn.request(self.method, self.path, self.params, self.headers)
        self.response.callback(conn.getresponse())

class Page(object):
    def __init__(self, size=25):
        self.index = 0
        self.size = size

    def get_path_params(self):
        me = { "limit":self.size, "offset":(self.index * self.size) }
        return urllib.urlencode(me)

class Sort(object):
    def __init__(self, column='id', direction='ASC'):
        self.column = column
        self.direction = direction

    def get_path_params(self):
        me = { "sort":self.column, "dir":self.direction }
        return urllib.urlencode(me)

class DynamicFinder(object):
    def __init__(self, finder=None, *args):
        self.finder = finder
        self.args = args

    def get_path_params(self):
        params = "/dynamic/%s?" % self.finder
        for arg in self.args:
            params += "args=%s&" % arg
        return params

class ProxyError(Exception):
    def __init__(self, message, error):
        self.message = "%s: %s" % (message, error)

    def __str__(self):
        return repr(self.message)

class Proxy(object):
    def __init__(self, url, database, table, model, pageSize=25):
        self.url = url
        self.database = database
        self.table = table
        self.path = "/jongo/%s/%s" % (database, table)
        self.model = model
        self.page = Page(pageSize)
        self.sort = None

    def create(self, model_instance):
        request = Request(self.url, self.path, 'POST', model_instance.toJSON())
        request.perform()
        if not request.response.success:
            raise ProxyError(request.response.message, request.response.code)

    def read_all(self, dynamicFinder=None):
        data = []
        path = "%s?%s" % (self.path, self.page.get_path_params())
        if self.sort:
            path = "%s?%s&%s" % (self.path, self.page.get_path_params(), self.sort.get_path_params())
        if dynamicFinder:
            path = "%s%s" % (self.path, dynamicFinder.get_path_params())
        request = Request(self.url, path)
        request.perform()
        if request.response.success:
            for response in request.response.data:
                model_instance = self.model()
                model_instance.map_response_data(response)
                data.append(model_instance)
        else:
            raise ProxyError(request.response.message, request.response.code)
        return data

    def read(self, id):
        instance_path = "%s/%d" % (self.path, id)
        request = Request(self.url, instance_path)
        request.perform()
        model_instance = None
        if request.response.success:
            model_instance = self.model()
            model_instance.map_response_data(request.response.data[0])
        return model_instance

    def update(self, instance):
        instance_path = "%s/%d" % (self.path, instance.id)
        request = Request(self.url, instance_path, 'PUT', instance.toJSON())
        request.perform()
        if not request.response.success:
            raise ProxyError(request.response.message, request.response.code)

    def delete(self, instance):
        instance_path = "%s/%d" % (self.path, instance.id)
        request = Request(self.url, instance_path, 'DELETE')
        request.perform()
        if not request.response.success:
            raise ProxyError(request.response.message, request.response.code)

    def query(self, query_name, *args):
        data = []
        query_path = "/%s/query/%s" % (self.database, query_name)
        if args is not None and len(args) > 0:
            query_path = "/%s/query/%s?args=%s" % (self.database, query_name, "&args=".join(map(str, args)))
        request = Request(self.url, query_path)
        request.perform()
        if request.response.success:
            for response in request.response.data:
                model_instance = self.model()
                model_instance.map_response_data(response)
                data.append(model_instance)
        else:
            raise ProxyError(request.response.message, request.response.error)
        return data

class JongoStore(object):
    def __init__(self, proxy=None, model=None, autoLoad=False, autoSync=False, data=None):
        self.proxy = proxy
        self.model = model
        self.autoLoad = autoLoad
        self.autoSync = autoSync
        self.data = data

        if autoLoad:
            self.load()

    def add(self, model_instance):
        model_instance.ghost = True
        self.data.append(model_instance)
        if self.autoSync:
            self.sync()

    def remove(self, model_instance):
        if model_instance in self.data:
            i = self.data.index(model_instance)
            model_instance.dead = True
            self.data[i] = model_instance
            if self.autoSync:
                self.sync()
        else:
            raise ValueError("The given model is not in the store")

    def update(self, model_instance):
        if model_instance in self.data:
            model_instance.dirty = True
            i = self.data.index(model_instance)
            self.data[i] = model_instance
            if self.autoSync:
                self.sync()
        else:
            raise ValueError("The given model is not in the store")

    def get_at(self, index):
        return self.data[index]

    def get_by_id(self, id):
        ret = None
        for i in self.data:
            if i.id == id:
                ret = i
        return ret

    def findBy(self, name, value):
        ret = None
        for instance in self.data:
            if getattr(instance, name) == value:
                ret = instance
                break
        return ret

    def findAllBy(self, name, value):
        ret = []
        for instance in self.data:
            if getattr(instance, name) == value:
                ret.append(instance)
        return ret

    def count(self):
        return len(self.data)

    def filter(self, f):
        return filter(f, self.data)

    def load(self, dynamicFinder=None):
        self.data = self.proxy.read_all(dynamicFinder)

    def sync(self):
        for instance in self.data:
            if instance.ghost:
                self.proxy.create(instance)
            elif instance.dirty:
                self.proxy.update(instance)
            elif instance.dead:
                self.proxy.delete(instance)
        self.load()

    def page(self, index=None):
        if index:
            self.proxy.page.index = index
            if self.autoLoad:
                self.load()
        return self.proxy.page.index

    def next_page(self):
        index = self.proxy.page.index + 1
        return self.page(index)

    def prev_page(self):
        index = self.proxy.page.index
        if index >= 0:
            index -= 1
        return self.page(index)

    def unsort(self):
        self.proxy.sort = None
        if self.autoLoad:
            self.load()

    def sort(self, column, direction):
        self.proxy.sort = Sort(column, direction)
        if self.autoLoad:
            self.load()

class JongoModel(object):
    def __init__(self, id=None, idCol=None, ghost=False, dirty=False, dead=False, proxy=None):
        self.id = id
        self.idCol = idCol
        self.ghost = ghost
        self.dirty = dirty
        self.dead = dead
        self.proxy = proxy

    def _get_unmappable_values(self):
        unmap = [ "proxy", "id", "idCol", "ghost", "dirty", "dead" ] 
        return unmap

    def set(self, attr, value):
        if attr not in self._get_unmappable_values():
            self.dirty = True
        object.__setattr__(self, attr, value)

    def map_response_data(self, data):
        if self.idCol:
            self.id = data[self.idCol]
        for attr, value in self.__dict__.iteritems():
            if attr in data:
                object.__setattr__(self, attr, data[attr])
    
    def toJSON(self):
        me = {}
        for attr, value in self.__dict__.iteritems():
            if attr not in self._get_unmappable_values():
                me[attr] = value
        return json.dumps(me)

    def __str__(self):
        me = []
        for attr, value in self.__dict__.iteritems():
            me.append("%s:%s" % (attr, value))
        return " ".join(me)

    def load(self):
        if not self.id:
            raise KeyError("Invalid id for the model")

        if not self.proxy:
            raise ValueError("Proxy has not been set for this model")

        return self.proxy.read(self.id)
        
    def commit(self):
        if not self.proxy:
            raise ValueError("Proxy has not been set for this model")

        if self.ghost:
            self.proxy.create(self)
        elif self.dirty:
            self.proxy.update(self)
        elif self.dead:
            self.proxy.remove(self)
        else:
            raise TypeError("Cannot commit non ghost, dirty or dead models")

    def destroy(self):
        if not self.id:
            raise KeyError("Invalid id for the model")

        if not self.proxy:
            raise ValueError("Proxy has not been set for this model")

        self.dead = True
        self.commit()
