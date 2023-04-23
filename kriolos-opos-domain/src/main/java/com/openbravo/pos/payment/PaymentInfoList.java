//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright © 2009-2020 uniCenta
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.payment;

import java.util.LinkedList;

public class PaymentInfoList {
    
    private final LinkedList<PaymentInfo> m_apayment;

    public PaymentInfoList() {
        m_apayment = new LinkedList<>();
    }
        
    /**
     * Get total in all PaymentInfo
     * @return 
     */
    public double getTotal() {
        
        double dTotal = 0.0;
        for (PaymentInfo p : m_apayment) {
            dTotal += p.getTotal();
        }
        
        return dTotal;
    }
    
    /**
     * Get total payd in all PaymentInfo
     * @return 
     */
    public double getPaidTotal() {
        
        double dTotal = 0.0;
        for (PaymentInfo p : m_apayment) {
            dTotal += p.getPaid();
        }
        
        return dTotal;
    }

    public boolean isEmpty() {
        return m_apayment.isEmpty();
    }
    
    public void add(PaymentInfo p) {
        m_apayment.addLast(p);
    }
    
    public void removeLast() {
        m_apayment.removeLast();
    }
    
    public LinkedList<PaymentInfo> getPayments() {
        return m_apayment;
    }
}
