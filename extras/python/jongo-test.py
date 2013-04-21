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

# An example Python script which uses json, httplib and urllib to do some
# operations on Jongo running in demo mode

import jongo

class User(jongo.JongoModel):
    def __init__(self, id=None, name=None, age=None):
        jongo.JongoModel.__init__(self)
        self.id = id
        self.name = name
        self.age = age

class UserStore(jongo.JongoStore):
    def __init__(self):
        jongo.JongoStore.__init__(self)
        self.model = User

class Car(jongo.JongoModel):
    def __init__(self, id=None, model=None, maker=None, fuel=None, transmission=None, year=None):
        jongo.JongoModel.__init__(self)
        self.id = id
        self.idCol = "cid"
        self.model = model
        self.maker = maker
        self.fuel = fuel
        self.transmission = transmission
        self.year = year

class CarStore(jongo.JongoStore):
    def __init__(self):
        jongo.JongoStore.__init__(self)
        self.model = Car
        self.proxy = jongo.Proxy("localhost:8080","/jongo/car", Car)

class MakerData(jongo.JongoModel):
    def __init__(self, id=None, maker=None, year=None, month=None, sales=None):
        jongo.JongoModel.__init__(self)
        self.id = id
        self.maker = maker
        self.year = year
        self.month = month
        self.sales = sales

class MakerDataStore(jongo.JongoStore):
    def __init__(self):
        jongo.JongoStore.__init__(self)
        self.model = MakerData
        self.proxy = jongo.Proxy("localhost:8080","/jongo/maker_stats", MakerData, 50)

if __name__ == '__main__':
    store = UserStore()
    store.proxy = jongo.Proxy("localhost:8080","/jongo/user", User)
    store.load()
    assert store.count() == 2

    u1 = User(None, 'kkk', 16)
    store.add(u1)
    assert store.count() == 3

    u1 = store.get_at(store.count() - 1)
    # "Before sync, the user instance is a ghost. This means it doesn't have a value in the db"
    assert u1.ghost == True
    assert u1.dirty == False

    # "Now we do the sync and the user should not be a ghost any more"
    store.sync()
    u1 = store.get_at(store.count() - 1)
    assert u1.ghost == False
    assert u1.dirty == False

    # "Let's change its name"
    u1.set("name", "ttt")
    assert u1.dirty == True
    store.update(u1)

    # "Before calling sync, the user should be dirty"
    u1 = store.get_at(store.count() - 1)
    assert u1.ghost == False
    assert u1.dirty == True

    # "After sync, we have the user with the new name and it's not dirty"
    store.sync()
    u1 = store.get_at(store.count() - 1)
    assert u1.ghost == False
    assert u1.dirty == False

    # "To delete a user, we don't remove it from the store. It will be marked as dead"
    store.remove(u1)
    u1 = store.get_at(store.count() - 1)
    assert u1.ghost == False
    assert u1.dirty == False
    assert u1.dead == True

    # "When the sync is performed, the element is removed from the db and from the store"
    store.sync()
    u2 = store.get_at(store.count() - 1)
    assert u1.id != u2.id

    # "Now with the cars which have a custom id which is mapped to our column ID"
    carstore = CarStore()
    carstore.load()
    assert carstore.count() == 3

    c1 = Car(None, "206cc", "Peugeot", "Gasoline", "Manual", 2005)
    carstore.add(c1)
    carstore.sync()
    assert carstore.count() == 4

    c1 = carstore.get_at(carstore.count() - 1) 
    c1.model = "206"
    c1.maker = "PPegoushn"
    carstore.update(c1)
    carstore.sync()
    assert carstore.count() == 4
    c1 = carstore.get_at(carstore.count() - 1) 
    carstore.remove(c1)
    assert carstore.count() == 4
    carstore.sync()
    assert carstore.count() == 3

    # lets test the sorting
    carstore.sort('model','DESC')
    carstore.load()
    c1 = carstore.get_at(0) 
    assert carstore.count() == 3
    assert c1.model == 'X5'

    carstore.sort('model','ASC')
    carstore.load()
    c1 = carstore.get_at(0) 
    assert carstore.count() == 3
    assert c1.model == 500

    carstore.unsort()
    carstore.load()
    c1 = carstore.get_at(0) 
    assert carstore.count() == 3
    assert c1.model == 'C2'
    
    #cars = carstore.filter('model','C2')
    #assert len(cars) == 1
    cars = carstore.filter(lambda x: x.model == 'C2')
    assert len(cars) == 1
    cars = carstore.filter(lambda x: x.year > 2007 and x.year <= 2010)
    assert len(cars) == 2

    # lets test the paging thing

    mds = MakerDataStore()
    mds.load()
    assert mds.count() == 50

    d1 = mds.get_at(0)
    assert d1.id == 0
    assert mds.page() == 0

    mds.page(5)
    mds.load()

    d1 = mds.get_at(0)
    assert mds.count() == 50
    assert d1.id == 250
    assert mds.page() == 5

    mds.next_page()
    mds.load()
    d1 = mds.get_at(0)
    assert mds.count() == 50
    assert d1.id == 300
    assert mds.page() == 6

    mds.prev_page()
    mds.prev_page()
    mds.load()
    d1 = mds.get_at(0)
    assert mds.count() == 50
    assert d1.id == 200
    assert mds.page() == 4

    print "All tests passed!"
