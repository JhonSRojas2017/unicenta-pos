//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.data.loader;

import com.openbravo.basic.BasicException;

/**
 *
 * @author adrianromero
 */
public class SerializerWriteParams  implements SerializerWrite<DataParams>{
    
    /**
     *
     */
    public static final SerializerWrite INSTANCE = new SerializerWriteParams();
    
    /**
     *
     * @param dp
     * @param obj
     * @throws BasicException
     */
    @Override
    public void writeValues(DataWrite dp, DataParams obj) throws BasicException {
        obj.setDataWrite(dp);
        obj.writeValues();
        obj.setDataWrite(null);
    }     
}