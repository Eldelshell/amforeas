/**
 * Copyright (C) 2011, 2012 Alejandro Ayuso
 *
 * This file is part of Amforeas.
 * Amforeas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * Amforeas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Amforeas.  If not, see <http://www.gnu.org/licenses/>.
 */
package amforeas.demo;

/**
 * An ENUM with demo data for the demo database.
 * @author Alejandro Ayuso 
 */
public enum CarMaker {
    ABARTH("Abarth"),
    ALFA("Alfa"),
    ROMEO("Romeo"),
    ALPINA("Alpina"),
    ARIEL("Ariel"),
    ASTON("Aston"),
    MARTIN("Martin"),
    AUDI("Audi"),
    BENTLEY("Bentley"),
    BMW("BMW"),
    CADILLAC("Cadillac"),
    CATERHAM("Caterham"),
    CHEVROLET("Chevrolet"),
    CHRYSLER("Chrysler"),
    CITROEN("Citroën"),
    CORVETTE("Corvette"),
    DAIHATSU("Daihatsu"),
    DODGE("Dodge"),
    FERRARI("Ferrari"),
    FIAT("Fiat"),
    FORD("Ford"),
    HONDA("Honda"),
    HYUNDAI("Hyundai"),
    INFINITI("Infiniti"),
    ISUZU("Isuzu"),
    JAGUAR("Jaguar"),
    JEEP("Jeep"),
    KIA("Kia"),
    KTM("KTM"),
    LAMBORGHINI("Lamborghini"),
    LAND("Land"),
    ROVER("Rover"),
    LEXUS("Lexus"),
    LOTUS("Lotus"),
    MASERATI("Maserati"),
    MAYBACH("Maybach"),
    MAZDA("Mazda"),
    MCLAREN("McLaren"),
    MERCEDESBENZ("Mercedes-Benz"),
    MG("MG"),
    MINI("MINI"),
    MITSUBISHI("Mitsubishi"),
    MORGAN("Morgan"),
    NISSAN("Nissan"),
    PERODUA("Perodua"),
    PEUGEOT("Peugeot"),
    PORSCHE("Porsche"),
    PROTON("Proton"),
    RENAULT("Renault"),
    ROLLSROYCE("Rolls-Royce"),
    SAAB("Saab"),
    SEAT("SEAT"),
    SKODA("Skoda"),
    SMART("smart"),
    SSANGYONG("SsangYong"),
    SUBARU("Subaru"),
    SUZUKI("Suzuki"),
    TESLA("Tesla"),
    TOYOTA("Toyota"),
    VAUXHALL("Vauxhall"),
    VOLKSWAGEN("Volkswagen"),
    VOLVO("Volvo");
    
    private final String realName;
    
    private CarMaker(String realName){
        this.realName = realName;
    }
    
    public String getRealName(){
        return this.realName;
    }

}
