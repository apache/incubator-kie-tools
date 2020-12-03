package com.google.gwt.i18n.client;

import com.google.gwt.i18n.client.impl.CurrencyDataImpl;
import com.google.gwt.core.client.JavaScriptObject;
import java.util.HashMap;

public class CurrencyList_ extends com.google.gwt.i18n.client.CurrencyList {
  
  @Override
  protected CurrencyData getDefaultJava() {
    return new CurrencyDataImpl("USD", "US$", 2, "US$", "$");
  }
  
  @Override
  protected native CurrencyData getDefaultNative() /*-{
    return [ "USD", "US$", 2, "US$", "$"];
  }-*/;
  
  @Override
  protected HashMap<String, CurrencyData> loadCurrencyMapJava() {
    HashMap<String, CurrencyData> result = super.loadCurrencyMapJava();
    // ADP
    result.put("ADP", new CurrencyDataImpl("ADP", "ADP", 128, "ADP", "ADP"));
    // AED
    result.put("AED", new CurrencyDataImpl("AED", "DH", 2, "DH", "dh"));
    // AFA
    result.put("AFA", new CurrencyDataImpl("AFA", "AFA", 130, "AFA", "AFA"));
    // AFN
    result.put("AFN", new CurrencyDataImpl("AFN", "AFN", 0, "AFN", "Af."));
    // ALK
    result.put("ALK", new CurrencyDataImpl("ALK", "ALK", 130, "ALK", "ALK"));
    // ALL
    result.put("ALL", new CurrencyDataImpl("ALL", "ALL", 0, "ALL", "Lek"));
    // AMD
    result.put("AMD", new CurrencyDataImpl("AMD", "AMD", 2, "AMD", "Dram"));
    // ANG
    result.put("ANG", new CurrencyDataImpl("ANG", "ANG", 2, "ANG", "ANG"));
    // AOA
    result.put("AOA", new CurrencyDataImpl("AOA", "AOA", 2, "AOA", "Kz"));
    // AOK
    result.put("AOK", new CurrencyDataImpl("AOK", "AOK", 130, "AOK", "AOK"));
    // AON
    result.put("AON", new CurrencyDataImpl("AON", "AON", 130, "AON", "AON"));
    // AOR
    result.put("AOR", new CurrencyDataImpl("AOR", "AOR", 130, "AOR", "AOR"));
    // ARA
    result.put("ARA", new CurrencyDataImpl("ARA", "ARA", 130, "ARA", "ARA"));
    // ARL
    result.put("ARL", new CurrencyDataImpl("ARL", "ARL", 130, "ARL", "ARL"));
    // ARM
    result.put("ARM", new CurrencyDataImpl("ARM", "ARM", 130, "ARM", "ARM"));
    // ARP
    result.put("ARP", new CurrencyDataImpl("ARP", "ARP", 130, "ARP", "ARP"));
    // ARS
    result.put("ARS", new CurrencyDataImpl("ARS", "AR$", 2, "AR$", "$"));
    // ATS
    result.put("ATS", new CurrencyDataImpl("ATS", "ATS", 130, "ATS", "ATS"));
    // AUD
    result.put("AUD", new CurrencyDataImpl("AUD", "A$", 2, "AU$", "$"));
    // AWG
    result.put("AWG", new CurrencyDataImpl("AWG", "AWG", 2, "AWG", "Afl."));
    // AZM
    result.put("AZM", new CurrencyDataImpl("AZM", "AZM", 130, "AZM", "AZM"));
    // AZN
    result.put("AZN", new CurrencyDataImpl("AZN", "AZN", 2, "AZN", "man."));
    // BAD
    result.put("BAD", new CurrencyDataImpl("BAD", "BAD", 130, "BAD", "BAD"));
    // BAM
    result.put("BAM", new CurrencyDataImpl("BAM", "BAM", 2, "BAM", "KM"));
    // BAN
    result.put("BAN", new CurrencyDataImpl("BAN", "BAN", 130, "BAN", "BAN"));
    // BBD
    result.put("BBD", new CurrencyDataImpl("BBD", "BBD", 2, "BBD", "$"));
    // BDT
    result.put("BDT", new CurrencyDataImpl("BDT", "Tk", 2, "Tk", "৳"));
    // BEC
    result.put("BEC", new CurrencyDataImpl("BEC", "BEC", 130, "BEC", "BEC"));
    // BEF
    result.put("BEF", new CurrencyDataImpl("BEF", "BEF", 130, "BEF", "BEF"));
    // BEL
    result.put("BEL", new CurrencyDataImpl("BEL", "BEL", 130, "BEL", "BEL"));
    // BGL
    result.put("BGL", new CurrencyDataImpl("BGL", "BGL", 130, "BGL", "BGL"));
    // BGM
    result.put("BGM", new CurrencyDataImpl("BGM", "BGM", 130, "BGM", "BGM"));
    // BGN
    result.put("BGN", new CurrencyDataImpl("BGN", "BGN", 2, "BGN", "lev"));
    // BGO
    result.put("BGO", new CurrencyDataImpl("BGO", "BGO", 130, "BGO", "BGO"));
    // BHD
    result.put("BHD", new CurrencyDataImpl("BHD", "BHD", 3, "BHD", "din"));
    // BIF
    result.put("BIF", new CurrencyDataImpl("BIF", "BIF", 0, "BIF", "FBu"));
    // BMD
    result.put("BMD", new CurrencyDataImpl("BMD", "BMD", 2, "BMD", "$"));
    // BND
    result.put("BND", new CurrencyDataImpl("BND", "BND", 2, "BND", "$"));
    // BOB
    result.put("BOB", new CurrencyDataImpl("BOB", "BOB", 2, "BOB", "Bs"));
    // BOL
    result.put("BOL", new CurrencyDataImpl("BOL", "BOL", 130, "BOL", "BOL"));
    // BOP
    result.put("BOP", new CurrencyDataImpl("BOP", "BOP", 130, "BOP", "BOP"));
    // BOV
    result.put("BOV", new CurrencyDataImpl("BOV", "BOV", 130, "BOV", "BOV"));
    // BRB
    result.put("BRB", new CurrencyDataImpl("BRB", "BRB", 130, "BRB", "BRB"));
    // BRC
    result.put("BRC", new CurrencyDataImpl("BRC", "BRC", 130, "BRC", "BRC"));
    // BRE
    result.put("BRE", new CurrencyDataImpl("BRE", "BRE", 130, "BRE", "BRE"));
    // BRL
    result.put("BRL", new CurrencyDataImpl("BRL", "R$", 2, "R$", "R$"));
    // BRN
    result.put("BRN", new CurrencyDataImpl("BRN", "BRN", 130, "BRN", "BRN"));
    // BRR
    result.put("BRR", new CurrencyDataImpl("BRR", "BRR", 130, "BRR", "BRR"));
    // BRZ
    result.put("BRZ", new CurrencyDataImpl("BRZ", "BRZ", 130, "BRZ", "BRZ"));
    // BSD
    result.put("BSD", new CurrencyDataImpl("BSD", "BSD", 2, "BSD", "$"));
    // BTN
    result.put("BTN", new CurrencyDataImpl("BTN", "BTN", 2, "BTN", "Nu."));
    // BUK
    result.put("BUK", new CurrencyDataImpl("BUK", "BUK", 130, "BUK", "BUK"));
    // BWP
    result.put("BWP", new CurrencyDataImpl("BWP", "BWP", 2, "BWP", "P"));
    // BYB
    result.put("BYB", new CurrencyDataImpl("BYB", "BYB", 130, "BYB", "BYB"));
    // BYN
    result.put("BYN", new CurrencyDataImpl("BYN", "BYN", 2, "BYN", "BYN"));
    // BYR
    result.put("BYR", new CurrencyDataImpl("BYR", "BYR", 128, "BYR", "BYR"));
    // BZD
    result.put("BZD", new CurrencyDataImpl("BZD", "BZD", 2, "BZD", "$"));
    // CAD
    result.put("CAD", new CurrencyDataImpl("CAD", "CA$", 2, "C$", "$"));
    // CDF
    result.put("CDF", new CurrencyDataImpl("CDF", "CDF", 2, "CDF", "FrCD"));
    // CHE
    result.put("CHE", new CurrencyDataImpl("CHE", "CHE", 130, "CHE", "CHE"));
    // CHF
    result.put("CHF", new CurrencyDataImpl("CHF", "CHF", 2, "CHF", "CHF"));
    // CHW
    result.put("CHW", new CurrencyDataImpl("CHW", "CHW", 130, "CHW", "CHW"));
    // CLE
    result.put("CLE", new CurrencyDataImpl("CLE", "CLE", 130, "CLE", "CLE"));
    // CLF
    result.put("CLF", new CurrencyDataImpl("CLF", "CLF", 132, "CLF", "CLF"));
    // CLP
    result.put("CLP", new CurrencyDataImpl("CLP", "CL$", 0, "CL$", "$"));
    // CNH
    result.put("CNH", new CurrencyDataImpl("CNH", "CNH", 130, "CNH", "CNH"));
    // CNX
    result.put("CNX", new CurrencyDataImpl("CNX", "CNX", 130, "CNX", "CNX"));
    // CNY
    result.put("CNY", new CurrencyDataImpl("CNY", "CN¥", 2, "RMB¥", "¥"));
    // COP
    result.put("COP", new CurrencyDataImpl("COP", "COL$", 2, "COL$", "$"));
    // COU
    result.put("COU", new CurrencyDataImpl("COU", "COU", 130, "COU", "COU"));
    // CRC
    result.put("CRC", new CurrencyDataImpl("CRC", "CR₡", 2, "CR₡", "₡"));
    // CSD
    result.put("CSD", new CurrencyDataImpl("CSD", "CSD", 130, "CSD", "CSD"));
    // CSK
    result.put("CSK", new CurrencyDataImpl("CSK", "CSK", 130, "CSK", "CSK"));
    // CUC
    result.put("CUC", new CurrencyDataImpl("CUC", "CUC", 2, "CUC", "$"));
    // CUP
    result.put("CUP", new CurrencyDataImpl("CUP", "$MN", 2, "$MN", "$"));
    // CVE
    result.put("CVE", new CurrencyDataImpl("CVE", "CVE", 2, "CVE", "CVE"));
    // CYP
    result.put("CYP", new CurrencyDataImpl("CYP", "CYP", 130, "CYP", "CYP"));
    // CZK
    result.put("CZK", new CurrencyDataImpl("CZK", "Kč", 2, "Kč", "Kč"));
    // DDM
    result.put("DDM", new CurrencyDataImpl("DDM", "DDM", 130, "DDM", "DDM"));
    // DEM
    result.put("DEM", new CurrencyDataImpl("DEM", "DEM", 130, "DEM", "DEM"));
    // DJF
    result.put("DJF", new CurrencyDataImpl("DJF", "Fdj", 0, "Fdj", "Fdj"));
    // DKK
    result.put("DKK", new CurrencyDataImpl("DKK", "kr", 2, "kr", "kr"));
    // DOP
    result.put("DOP", new CurrencyDataImpl("DOP", "RD$", 2, "RD$", "$"));
    // DZD
    result.put("DZD", new CurrencyDataImpl("DZD", "DZD", 2, "DZD", "din"));
    // ECS
    result.put("ECS", new CurrencyDataImpl("ECS", "ECS", 130, "ECS", "ECS"));
    // ECV
    result.put("ECV", new CurrencyDataImpl("ECV", "ECV", 130, "ECV", "ECV"));
    // EEK
    result.put("EEK", new CurrencyDataImpl("EEK", "EEK", 130, "EEK", "EEK"));
    // EGP
    result.put("EGP", new CurrencyDataImpl("EGP", "LE", 2, "LE", "E£"));
    // ERN
    result.put("ERN", new CurrencyDataImpl("ERN", "ERN", 2, "ERN", "Nfk"));
    // ESA
    result.put("ESA", new CurrencyDataImpl("ESA", "ESA", 130, "ESA", "ESA"));
    // ESB
    result.put("ESB", new CurrencyDataImpl("ESB", "ESB", 130, "ESB", "ESB"));
    // ESP
    result.put("ESP", new CurrencyDataImpl("ESP", "ESP", 128, "ESP", "ESP"));
    // ETB
    result.put("ETB", new CurrencyDataImpl("ETB", "ETB", 2, "ETB", "Birr"));
    // EUR
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€", "€"));
    // FIM
    result.put("FIM", new CurrencyDataImpl("FIM", "FIM", 130, "FIM", "FIM"));
    // FJD
    result.put("FJD", new CurrencyDataImpl("FJD", "FJD", 2, "FJD", "$"));
    // FKP
    result.put("FKP", new CurrencyDataImpl("FKP", "FKP", 2, "FKP", "£"));
    // FRF
    result.put("FRF", new CurrencyDataImpl("FRF", "FRF", 130, "FRF", "FRF"));
    // GBP
    result.put("GBP", new CurrencyDataImpl("GBP", "£", 2, "GB£", "£"));
    // GEK
    result.put("GEK", new CurrencyDataImpl("GEK", "GEK", 130, "GEK", "GEK"));
    // GEL
    result.put("GEL", new CurrencyDataImpl("GEL", "GEL", 2, "GEL", "GEL"));
    // GHC
    result.put("GHC", new CurrencyDataImpl("GHC", "GHC", 130, "GHC", "GHC"));
    // GHS
    result.put("GHS", new CurrencyDataImpl("GHS", "GHS", 2, "GHS", "GHS"));
    // GIP
    result.put("GIP", new CurrencyDataImpl("GIP", "GIP", 2, "GIP", "£"));
    // GMD
    result.put("GMD", new CurrencyDataImpl("GMD", "GMD", 2, "GMD", "GMD"));
    // GNF
    result.put("GNF", new CurrencyDataImpl("GNF", "GNF", 0, "GNF", "FG"));
    // GNS
    result.put("GNS", new CurrencyDataImpl("GNS", "GNS", 130, "GNS", "GNS"));
    // GQE
    result.put("GQE", new CurrencyDataImpl("GQE", "GQE", 130, "GQE", "GQE"));
    // GRD
    result.put("GRD", new CurrencyDataImpl("GRD", "GRD", 130, "GRD", "GRD"));
    // GTQ
    result.put("GTQ", new CurrencyDataImpl("GTQ", "GTQ", 2, "GTQ", "Q"));
    // GWE
    result.put("GWE", new CurrencyDataImpl("GWE", "GWE", 130, "GWE", "GWE"));
    // GWP
    result.put("GWP", new CurrencyDataImpl("GWP", "GWP", 130, "GWP", "GWP"));
    // GYD
    result.put("GYD", new CurrencyDataImpl("GYD", "GYD", 2, "GYD", "$"));
    // HKD
    result.put("HKD", new CurrencyDataImpl("HKD", "HK$", 2, "HK$", "$"));
    // HNL
    result.put("HNL", new CurrencyDataImpl("HNL", "L", 2, "L", "L"));
    // HRD
    result.put("HRD", new CurrencyDataImpl("HRD", "HRD", 130, "HRD", "HRD"));
    // HRK
    result.put("HRK", new CurrencyDataImpl("HRK", "HRK", 2, "HRK", "kn"));
    // HTG
    result.put("HTG", new CurrencyDataImpl("HTG", "HTG", 2, "HTG", "HTG"));
    // HUF
    result.put("HUF", new CurrencyDataImpl("HUF", "HUF", 2, "HUF", "Ft"));
    // IDR
    result.put("IDR", new CurrencyDataImpl("IDR", "IDR", 2, "IDR", "Rp"));
    // IEP
    result.put("IEP", new CurrencyDataImpl("IEP", "IEP", 130, "IEP", "IEP"));
    // ILP
    result.put("ILP", new CurrencyDataImpl("ILP", "ILP", 130, "ILP", "ILP"));
    // ILR
    result.put("ILR", new CurrencyDataImpl("ILR", "ILR", 130, "ILR", "ILR"));
    // ILS
    result.put("ILS", new CurrencyDataImpl("ILS", "₪", 2, "IL₪", "₪"));
    // INR
    result.put("INR", new CurrencyDataImpl("INR", "₹", 2, "Rs", "₹"));
    // IQD
    result.put("IQD", new CurrencyDataImpl("IQD", "IQD", 0, "IQD", "din"));
    // IRR
    result.put("IRR", new CurrencyDataImpl("IRR", "IRR", 0, "IRR", "Rial"));
    // ISJ
    result.put("ISJ", new CurrencyDataImpl("ISJ", "ISJ", 130, "ISJ", "ISJ"));
    // ISK
    result.put("ISK", new CurrencyDataImpl("ISK", "kr", 0, "kr", "kr"));
    // ITL
    result.put("ITL", new CurrencyDataImpl("ITL", "ITL", 128, "ITL", "ITL"));
    // JMD
    result.put("JMD", new CurrencyDataImpl("JMD", "JA$", 2, "JA$", "$"));
    // JOD
    result.put("JOD", new CurrencyDataImpl("JOD", "JOD", 3, "JOD", "din"));
    // JPY
    result.put("JPY", new CurrencyDataImpl("JPY", "JP¥", 0, "JP¥", "¥"));
    // KES
    result.put("KES", new CurrencyDataImpl("KES", "Ksh", 2, "Ksh", "Ksh"));
    // KGS
    result.put("KGS", new CurrencyDataImpl("KGS", "KGS", 2, "KGS", "KGS"));
    // KHR
    result.put("KHR", new CurrencyDataImpl("KHR", "KHR", 2, "KHR", "Riel"));
    // KMF
    result.put("KMF", new CurrencyDataImpl("KMF", "KMF", 0, "KMF", "CF"));
    // KPW
    result.put("KPW", new CurrencyDataImpl("KPW", "KPW", 0, "KPW", "₩"));
    // KRH
    result.put("KRH", new CurrencyDataImpl("KRH", "KRH", 130, "KRH", "KRH"));
    // KRO
    result.put("KRO", new CurrencyDataImpl("KRO", "KRO", 130, "KRO", "KRO"));
    // KRW
    result.put("KRW", new CurrencyDataImpl("KRW", "₩", 0, "KR₩", "₩"));
    // KWD
    result.put("KWD", new CurrencyDataImpl("KWD", "KWD", 3, "KWD", "din"));
    // KYD
    result.put("KYD", new CurrencyDataImpl("KYD", "KYD", 2, "KYD", "$"));
    // KZT
    result.put("KZT", new CurrencyDataImpl("KZT", "KZT", 2, "KZT", "₸"));
    // LAK
    result.put("LAK", new CurrencyDataImpl("LAK", "LAK", 0, "LAK", "₭"));
    // LBP
    result.put("LBP", new CurrencyDataImpl("LBP", "LBP", 0, "LBP", "L£"));
    // LKR
    result.put("LKR", new CurrencyDataImpl("LKR", "SLRs", 2, "SLRs", "Rs"));
    // LRD
    result.put("LRD", new CurrencyDataImpl("LRD", "LRD", 2, "LRD", "$"));
    // LSL
    result.put("LSL", new CurrencyDataImpl("LSL", "LSL", 2, "LSL", "LSL"));
    // LTL
    result.put("LTL", new CurrencyDataImpl("LTL", "LTL", 130, "LTL", "Lt"));
    // LTT
    result.put("LTT", new CurrencyDataImpl("LTT", "LTT", 130, "LTT", "LTT"));
    // LUC
    result.put("LUC", new CurrencyDataImpl("LUC", "LUC", 130, "LUC", "LUC"));
    // LUF
    result.put("LUF", new CurrencyDataImpl("LUF", "LUF", 128, "LUF", "LUF"));
    // LUL
    result.put("LUL", new CurrencyDataImpl("LUL", "LUL", 130, "LUL", "LUL"));
    // LVL
    result.put("LVL", new CurrencyDataImpl("LVL", "LVL", 130, "LVL", "Ls"));
    // LVR
    result.put("LVR", new CurrencyDataImpl("LVR", "LVR", 130, "LVR", "LVR"));
    // LYD
    result.put("LYD", new CurrencyDataImpl("LYD", "LYD", 3, "LYD", "din"));
    // MAD
    result.put("MAD", new CurrencyDataImpl("MAD", "MAD", 2, "MAD", "MAD"));
    // MAF
    result.put("MAF", new CurrencyDataImpl("MAF", "MAF", 130, "MAF", "MAF"));
    // MCF
    result.put("MCF", new CurrencyDataImpl("MCF", "MCF", 130, "MCF", "MCF"));
    // MDC
    result.put("MDC", new CurrencyDataImpl("MDC", "MDC", 130, "MDC", "MDC"));
    // MDL
    result.put("MDL", new CurrencyDataImpl("MDL", "MDL", 2, "MDL", "MDL"));
    // MGA
    result.put("MGA", new CurrencyDataImpl("MGA", "MGA", 0, "MGA", "Ar"));
    // MGF
    result.put("MGF", new CurrencyDataImpl("MGF", "MGF", 128, "MGF", "MGF"));
    // MKD
    result.put("MKD", new CurrencyDataImpl("MKD", "MKD", 2, "MKD", "din"));
    // MKN
    result.put("MKN", new CurrencyDataImpl("MKN", "MKN", 130, "MKN", "MKN"));
    // MLF
    result.put("MLF", new CurrencyDataImpl("MLF", "MLF", 130, "MLF", "MLF"));
    // MMK
    result.put("MMK", new CurrencyDataImpl("MMK", "MMK", 0, "MMK", "K"));
    // MNT
    result.put("MNT", new CurrencyDataImpl("MNT", "MN₮", 2, "MN₮", "₮"));
    // MOP
    result.put("MOP", new CurrencyDataImpl("MOP", "MOP", 2, "MOP", "MOP"));
    // MRO
    result.put("MRO", new CurrencyDataImpl("MRO", "MRO", 128, "MRO", "MRO"));
    // MRU
    result.put("MRU", new CurrencyDataImpl("MRU", "MRU", 2, "MRU", "MRU"));
    // MTL
    result.put("MTL", new CurrencyDataImpl("MTL", "MTL", 130, "MTL", "MTL"));
    // MTP
    result.put("MTP", new CurrencyDataImpl("MTP", "MTP", 130, "MTP", "MTP"));
    // MUR
    result.put("MUR", new CurrencyDataImpl("MUR", "MUR", 2, "MUR", "Rs"));
    // MVP
    result.put("MVP", new CurrencyDataImpl("MVP", "MVP", 130, "MVP", "MVP"));
    // MVR
    result.put("MVR", new CurrencyDataImpl("MVR", "MVR", 2, "MVR", "MVR"));
    // MWK
    result.put("MWK", new CurrencyDataImpl("MWK", "MWK", 2, "MWK", "MWK"));
    // MXN
    result.put("MXN", new CurrencyDataImpl("MXN", "MX$", 2, "Mex$", "$"));
    // MXP
    result.put("MXP", new CurrencyDataImpl("MXP", "MXP", 130, "MXP", "MXP"));
    // MXV
    result.put("MXV", new CurrencyDataImpl("MXV", "MXV", 130, "MXV", "MXV"));
    // MYR
    result.put("MYR", new CurrencyDataImpl("MYR", "RM", 2, "RM", "RM"));
    // MZE
    result.put("MZE", new CurrencyDataImpl("MZE", "MZE", 130, "MZE", "MZE"));
    // MZM
    result.put("MZM", new CurrencyDataImpl("MZM", "MZM", 130, "MZM", "MZM"));
    // MZN
    result.put("MZN", new CurrencyDataImpl("MZN", "MZN", 2, "MZN", "MTn"));
    // NAD
    result.put("NAD", new CurrencyDataImpl("NAD", "NAD", 2, "NAD", "$"));
    // NGN
    result.put("NGN", new CurrencyDataImpl("NGN", "NGN", 2, "NGN", "₦"));
    // NIC
    result.put("NIC", new CurrencyDataImpl("NIC", "NIC", 130, "NIC", "NIC"));
    // NIO
    result.put("NIO", new CurrencyDataImpl("NIO", "NIO", 2, "NIO", "C$"));
    // NLG
    result.put("NLG", new CurrencyDataImpl("NLG", "NLG", 130, "NLG", "NLG"));
    // NOK
    result.put("NOK", new CurrencyDataImpl("NOK", "NOkr", 2, "NOkr", "kr"));
    // NPR
    result.put("NPR", new CurrencyDataImpl("NPR", "NPR", 2, "NPR", "Rs"));
    // NZD
    result.put("NZD", new CurrencyDataImpl("NZD", "NZ$", 2, "NZ$", "$"));
    // OMR
    result.put("OMR", new CurrencyDataImpl("OMR", "OMR", 3, "OMR", "Rial"));
    // PAB
    result.put("PAB", new CurrencyDataImpl("PAB", "B/.", 2, "B/.", "B/."));
    // PEI
    result.put("PEI", new CurrencyDataImpl("PEI", "PEI", 130, "PEI", "PEI"));
    // PEN
    result.put("PEN", new CurrencyDataImpl("PEN", "S/.", 2, "S/.", "S/."));
    // PES
    result.put("PES", new CurrencyDataImpl("PES", "PES", 130, "PES", "PES"));
    // PGK
    result.put("PGK", new CurrencyDataImpl("PGK", "PGK", 2, "PGK", "PGK"));
    // PHP
    result.put("PHP", new CurrencyDataImpl("PHP", "PHP", 2, "PHP", "₱"));
    // PKR
    result.put("PKR", new CurrencyDataImpl("PKR", "PKRs.", 2, "PKRs.", "Rs"));
    // PLN
    result.put("PLN", new CurrencyDataImpl("PLN", "PLN", 2, "PLN", "zł"));
    // PLZ
    result.put("PLZ", new CurrencyDataImpl("PLZ", "PLZ", 130, "PLZ", "PLZ"));
    // PTE
    result.put("PTE", new CurrencyDataImpl("PTE", "PTE", 130, "PTE", "PTE"));
    // PYG
    result.put("PYG", new CurrencyDataImpl("PYG", "PYG", 0, "PYG", "Gs"));
    // QAR
    result.put("QAR", new CurrencyDataImpl("QAR", "QAR", 2, "QAR", "Rial"));
    // RHD
    result.put("RHD", new CurrencyDataImpl("RHD", "RHD", 130, "RHD", "RHD"));
    // ROL
    result.put("ROL", new CurrencyDataImpl("ROL", "ROL", 130, "ROL", "ROL"));
    // RON
    result.put("RON", new CurrencyDataImpl("RON", "RON", 2, "RON", "RON"));
    // RSD
    result.put("RSD", new CurrencyDataImpl("RSD", "RSD", 0, "RSD", "din"));
    // RUB
    result.put("RUB", new CurrencyDataImpl("RUB", "руб.", 2, "руб.", "руб."));
    // RUR
    result.put("RUR", new CurrencyDataImpl("RUR", "RUR", 130, "RUR", "RUR"));
    // RWF
    result.put("RWF", new CurrencyDataImpl("RWF", "RWF", 0, "RWF", "RF"));
    // SAR
    result.put("SAR", new CurrencyDataImpl("SAR", "SR", 2, "SR", "Rial"));
    // SBD
    result.put("SBD", new CurrencyDataImpl("SBD", "SBD", 2, "SBD", "$"));
    // SCR
    result.put("SCR", new CurrencyDataImpl("SCR", "SCR", 2, "SCR", "SCR"));
    // SDD
    result.put("SDD", new CurrencyDataImpl("SDD", "SDD", 130, "SDD", "SDD"));
    // SDG
    result.put("SDG", new CurrencyDataImpl("SDG", "SDG", 2, "SDG", "SDG"));
    // SDP
    result.put("SDP", new CurrencyDataImpl("SDP", "SDP", 130, "SDP", "SDP"));
    // SEK
    result.put("SEK", new CurrencyDataImpl("SEK", "kr", 2, "kr", "kr"));
    // SGD
    result.put("SGD", new CurrencyDataImpl("SGD", "S$", 2, "S$", "$"));
    // SHP
    result.put("SHP", new CurrencyDataImpl("SHP", "SHP", 2, "SHP", "£"));
    // SIT
    result.put("SIT", new CurrencyDataImpl("SIT", "SIT", 130, "SIT", "SIT"));
    // SKK
    result.put("SKK", new CurrencyDataImpl("SKK", "SKK", 130, "SKK", "SKK"));
    // SLL
    result.put("SLL", new CurrencyDataImpl("SLL", "SLL", 0, "SLL", "SLL"));
    // SOS
    result.put("SOS", new CurrencyDataImpl("SOS", "SOS", 0, "SOS", "SOS"));
    // SRD
    result.put("SRD", new CurrencyDataImpl("SRD", "SRD", 2, "SRD", "$"));
    // SRG
    result.put("SRG", new CurrencyDataImpl("SRG", "SRG", 130, "SRG", "SRG"));
    // SSP
    result.put("SSP", new CurrencyDataImpl("SSP", "SSP", 2, "SSP", "SSP"));
    // STD
    result.put("STD", new CurrencyDataImpl("STD", "STD", 128, "STD", "Db"));
    // STN
    result.put("STN", new CurrencyDataImpl("STN", "STN", 2, "STN", "STN"));
    // SUR
    result.put("SUR", new CurrencyDataImpl("SUR", "SUR", 130, "SUR", "SUR"));
    // SVC
    result.put("SVC", new CurrencyDataImpl("SVC", "SVC", 130, "SVC", "SVC"));
    // SYP
    result.put("SYP", new CurrencyDataImpl("SYP", "SYP", 0, "SYP", "£"));
    // SZL
    result.put("SZL", new CurrencyDataImpl("SZL", "SZL", 2, "SZL", "SZL"));
    // THB
    result.put("THB", new CurrencyDataImpl("THB", "THB", 2, "THB", "฿"));
    // TJR
    result.put("TJR", new CurrencyDataImpl("TJR", "TJR", 130, "TJR", "TJR"));
    // TJS
    result.put("TJS", new CurrencyDataImpl("TJS", "TJS", 2, "TJS", "Som"));
    // TMM
    result.put("TMM", new CurrencyDataImpl("TMM", "TMM", 128, "TMM", "TMM"));
    // TMT
    result.put("TMT", new CurrencyDataImpl("TMT", "TMT", 2, "TMT", "TMT"));
    // TND
    result.put("TND", new CurrencyDataImpl("TND", "TND", 3, "TND", "din"));
    // TOP
    result.put("TOP", new CurrencyDataImpl("TOP", "TOP", 2, "TOP", "T$"));
    // TPE
    result.put("TPE", new CurrencyDataImpl("TPE", "TPE", 130, "TPE", "TPE"));
    // TRL
    result.put("TRL", new CurrencyDataImpl("TRL", "TRL", 128, "TRL", "TRL"));
    // TRY
    result.put("TRY", new CurrencyDataImpl("TRY", "TL", 2, "YTL", "TL"));
    // TTD
    result.put("TTD", new CurrencyDataImpl("TTD", "TTD", 2, "TTD", "$"));
    // TWD
    result.put("TWD", new CurrencyDataImpl("TWD", "NT$", 2, "NT$", "NT$"));
    // TZS
    result.put("TZS", new CurrencyDataImpl("TZS", "TZS", 2, "TZS", "TSh"));
    // UAH
    result.put("UAH", new CurrencyDataImpl("UAH", "UAH", 2, "UAH", "₴"));
    // UAK
    result.put("UAK", new CurrencyDataImpl("UAK", "UAK", 130, "UAK", "UAK"));
    // UGS
    result.put("UGS", new CurrencyDataImpl("UGS", "UGS", 130, "UGS", "UGS"));
    // UGX
    result.put("UGX", new CurrencyDataImpl("UGX", "UGX", 0, "UGX", "UGX"));
    // USD
    result.put("USD", new CurrencyDataImpl("USD", "US$", 2, "US$", "$"));
    // USN
    result.put("USN", new CurrencyDataImpl("USN", "USN", 130, "USN", "USN"));
    // USS
    result.put("USS", new CurrencyDataImpl("USS", "USS", 130, "USS", "USS"));
    // UYI
    result.put("UYI", new CurrencyDataImpl("UYI", "UYI", 128, "UYI", "UYI"));
    // UYP
    result.put("UYP", new CurrencyDataImpl("UYP", "UYP", 130, "UYP", "UYP"));
    // UYU
    result.put("UYU", new CurrencyDataImpl("UYU", "UY$", 2, "UY$", "$"));
    // UYW
    result.put("UYW", new CurrencyDataImpl("UYW", "UYW", 132, "UYW", "UYW"));
    // UZS
    result.put("UZS", new CurrencyDataImpl("UZS", "UZS", 2, "UZS", "soʼm"));
    // VEB
    result.put("VEB", new CurrencyDataImpl("VEB", "VEB", 130, "VEB", "VEB"));
    // VEF
    result.put("VEF", new CurrencyDataImpl("VEF", "VEF", 130, "VEF", "Bs"));
    // VES
    result.put("VES", new CurrencyDataImpl("VES", "VES", 2, "VES", "VES"));
    // VND
    result.put("VND", new CurrencyDataImpl("VND", "₫", 24, "₫", "₫"));
    // VNN
    result.put("VNN", new CurrencyDataImpl("VNN", "VNN", 130, "VNN", "VNN"));
    // VUV
    result.put("VUV", new CurrencyDataImpl("VUV", "VUV", 0, "VUV", "VUV"));
    // WST
    result.put("WST", new CurrencyDataImpl("WST", "WST", 2, "WST", "WST"));
    // XAF
    result.put("XAF", new CurrencyDataImpl("XAF", "FCFA", 0, "FCFA", "FCFA"));
    // XAG
    result.put("XAG", new CurrencyDataImpl("XAG", "XAG", 130, "XAG", "XAG"));
    // XAU
    result.put("XAU", new CurrencyDataImpl("XAU", "XAU", 130, "XAU", "XAU"));
    // XBA
    result.put("XBA", new CurrencyDataImpl("XBA", "XBA", 130, "XBA", "XBA"));
    // XBB
    result.put("XBB", new CurrencyDataImpl("XBB", "XBB", 130, "XBB", "XBB"));
    // XBC
    result.put("XBC", new CurrencyDataImpl("XBC", "XBC", 130, "XBC", "XBC"));
    // XBD
    result.put("XBD", new CurrencyDataImpl("XBD", "XBD", 130, "XBD", "XBD"));
    // XCD
    result.put("XCD", new CurrencyDataImpl("XCD", "EC$", 2, "EC$", "$"));
    // XDR
    result.put("XDR", new CurrencyDataImpl("XDR", "XDR", 130, "XDR", "XDR"));
    // XEU
    result.put("XEU", new CurrencyDataImpl("XEU", "XEU", 130, "XEU", "XEU"));
    // XFO
    result.put("XFO", new CurrencyDataImpl("XFO", "XFO", 130, "XFO", "XFO"));
    // XFU
    result.put("XFU", new CurrencyDataImpl("XFU", "XFU", 130, "XFU", "XFU"));
    // XOF
    result.put("XOF", new CurrencyDataImpl("XOF", "CFA", 0, "CFA", "CFA"));
    // XPD
    result.put("XPD", new CurrencyDataImpl("XPD", "XPD", 130, "XPD", "XPD"));
    // XPF
    result.put("XPF", new CurrencyDataImpl("XPF", "CFPF", 0, "CFPF", "FCFP"));
    // XPT
    result.put("XPT", new CurrencyDataImpl("XPT", "XPT", 130, "XPT", "XPT"));
    // XRE
    result.put("XRE", new CurrencyDataImpl("XRE", "XRE", 130, "XRE", "XRE"));
    // XSU
    result.put("XSU", new CurrencyDataImpl("XSU", "XSU", 130, "XSU", "XSU"));
    // XTS
    result.put("XTS", new CurrencyDataImpl("XTS", "XTS", 130, "XTS", "XTS"));
    // XUA
    result.put("XUA", new CurrencyDataImpl("XUA", "XUA", 130, "XUA", "XUA"));
    // XXX
    result.put("XXX", new CurrencyDataImpl("XXX", "¤", 130, "¤", "¤"));
    // YDD
    result.put("YDD", new CurrencyDataImpl("YDD", "YDD", 130, "YDD", "YDD"));
    // YER
    result.put("YER", new CurrencyDataImpl("YER", "YER", 0, "YER", "Rial"));
    // YUD
    result.put("YUD", new CurrencyDataImpl("YUD", "YUD", 130, "YUD", "YUD"));
    // YUM
    result.put("YUM", new CurrencyDataImpl("YUM", "YUM", 130, "YUM", "YUM"));
    // YUN
    result.put("YUN", new CurrencyDataImpl("YUN", "YUN", 130, "YUN", "YUN"));
    // YUR
    result.put("YUR", new CurrencyDataImpl("YUR", "YUR", 130, "YUR", "YUR"));
    // ZAL
    result.put("ZAL", new CurrencyDataImpl("ZAL", "ZAL", 130, "ZAL", "ZAL"));
    // ZAR
    result.put("ZAR", new CurrencyDataImpl("ZAR", "ZAR", 2, "ZAR", "R"));
    // ZMK
    result.put("ZMK", new CurrencyDataImpl("ZMK", "ZMK", 128, "ZMK", "ZWK"));
    // ZMW
    result.put("ZMW", new CurrencyDataImpl("ZMW", "ZMW", 2, "ZMW", "ZMW"));
    // ZRN
    result.put("ZRN", new CurrencyDataImpl("ZRN", "ZRN", 130, "ZRN", "ZRN"));
    // ZRZ
    result.put("ZRZ", new CurrencyDataImpl("ZRZ", "ZRZ", 130, "ZRZ", "ZRZ"));
    // ZWD
    result.put("ZWD", new CurrencyDataImpl("ZWD", "ZWD", 128, "ZWD", "ZWD"));
    // ZWL
    result.put("ZWL", new CurrencyDataImpl("ZWL", "ZWL", 130, "ZWL", "ZWL"));
    // ZWR
    result.put("ZWR", new CurrencyDataImpl("ZWR", "ZWR", 130, "ZWR", "ZWR"));
    return result;
  }
  
  @Override
  protected JavaScriptObject loadCurrencyMapNative() {
    return overrideMap(super.loadCurrencyMapNative(), loadMyCurrencyMapOverridesNative());
  }
  
  private native JavaScriptObject loadMyCurrencyMapOverridesNative() /*-{
    return {
      // ADP
      "ADP": [ "ADP", "ADP", 128, "ADP", "ADP"],
      // AED
      "AED": [ "AED", "DH", 2, "DH", "dh"],
      // AFA
      "AFA": [ "AFA", "AFA", 130, "AFA", "AFA"],
      // AFN
      "AFN": [ "AFN", "AFN", 0, "AFN", "Af."],
      // ALK
      "ALK": [ "ALK", "ALK", 130, "ALK", "ALK"],
      // ALL
      "ALL": [ "ALL", "ALL", 0, "ALL", "Lek"],
      // AMD
      "AMD": [ "AMD", "AMD", 2, "AMD", "Dram"],
      // ANG
      "ANG": [ "ANG", "ANG", 2, "ANG", "ANG"],
      // AOA
      "AOA": [ "AOA", "AOA", 2, "AOA", "Kz"],
      // AOK
      "AOK": [ "AOK", "AOK", 130, "AOK", "AOK"],
      // AON
      "AON": [ "AON", "AON", 130, "AON", "AON"],
      // AOR
      "AOR": [ "AOR", "AOR", 130, "AOR", "AOR"],
      // ARA
      "ARA": [ "ARA", "ARA", 130, "ARA", "ARA"],
      // ARL
      "ARL": [ "ARL", "ARL", 130, "ARL", "ARL"],
      // ARM
      "ARM": [ "ARM", "ARM", 130, "ARM", "ARM"],
      // ARP
      "ARP": [ "ARP", "ARP", 130, "ARP", "ARP"],
      // ARS
      "ARS": [ "ARS", "AR$", 2, "AR$", "$"],
      // ATS
      "ATS": [ "ATS", "ATS", 130, "ATS", "ATS"],
      // AUD
      "AUD": [ "AUD", "A$", 2, "AU$", "$"],
      // AWG
      "AWG": [ "AWG", "AWG", 2, "AWG", "Afl."],
      // AZM
      "AZM": [ "AZM", "AZM", 130, "AZM", "AZM"],
      // AZN
      "AZN": [ "AZN", "AZN", 2, "AZN", "man."],
      // BAD
      "BAD": [ "BAD", "BAD", 130, "BAD", "BAD"],
      // BAM
      "BAM": [ "BAM", "BAM", 2, "BAM", "KM"],
      // BAN
      "BAN": [ "BAN", "BAN", 130, "BAN", "BAN"],
      // BBD
      "BBD": [ "BBD", "BBD", 2, "BBD", "$"],
      // BDT
      "BDT": [ "BDT", "Tk", 2, "Tk", "৳"],
      // BEC
      "BEC": [ "BEC", "BEC", 130, "BEC", "BEC"],
      // BEF
      "BEF": [ "BEF", "BEF", 130, "BEF", "BEF"],
      // BEL
      "BEL": [ "BEL", "BEL", 130, "BEL", "BEL"],
      // BGL
      "BGL": [ "BGL", "BGL", 130, "BGL", "BGL"],
      // BGM
      "BGM": [ "BGM", "BGM", 130, "BGM", "BGM"],
      // BGN
      "BGN": [ "BGN", "BGN", 2, "BGN", "lev"],
      // BGO
      "BGO": [ "BGO", "BGO", 130, "BGO", "BGO"],
      // BHD
      "BHD": [ "BHD", "BHD", 3, "BHD", "din"],
      // BIF
      "BIF": [ "BIF", "BIF", 0, "BIF", "FBu"],
      // BMD
      "BMD": [ "BMD", "BMD", 2, "BMD", "$"],
      // BND
      "BND": [ "BND", "BND", 2, "BND", "$"],
      // BOB
      "BOB": [ "BOB", "BOB", 2, "BOB", "Bs"],
      // BOL
      "BOL": [ "BOL", "BOL", 130, "BOL", "BOL"],
      // BOP
      "BOP": [ "BOP", "BOP", 130, "BOP", "BOP"],
      // BOV
      "BOV": [ "BOV", "BOV", 130, "BOV", "BOV"],
      // BRB
      "BRB": [ "BRB", "BRB", 130, "BRB", "BRB"],
      // BRC
      "BRC": [ "BRC", "BRC", 130, "BRC", "BRC"],
      // BRE
      "BRE": [ "BRE", "BRE", 130, "BRE", "BRE"],
      // BRL
      "BRL": [ "BRL", "R$", 2, "R$", "R$"],
      // BRN
      "BRN": [ "BRN", "BRN", 130, "BRN", "BRN"],
      // BRR
      "BRR": [ "BRR", "BRR", 130, "BRR", "BRR"],
      // BRZ
      "BRZ": [ "BRZ", "BRZ", 130, "BRZ", "BRZ"],
      // BSD
      "BSD": [ "BSD", "BSD", 2, "BSD", "$"],
      // BTN
      "BTN": [ "BTN", "BTN", 2, "BTN", "Nu."],
      // BUK
      "BUK": [ "BUK", "BUK", 130, "BUK", "BUK"],
      // BWP
      "BWP": [ "BWP", "BWP", 2, "BWP", "P"],
      // BYB
      "BYB": [ "BYB", "BYB", 130, "BYB", "BYB"],
      // BYN
      "BYN": [ "BYN", "BYN", 2, "BYN", "BYN"],
      // BYR
      "BYR": [ "BYR", "BYR", 128, "BYR", "BYR"],
      // BZD
      "BZD": [ "BZD", "BZD", 2, "BZD", "$"],
      // CAD
      "CAD": [ "CAD", "CA$", 2, "C$", "$"],
      // CDF
      "CDF": [ "CDF", "CDF", 2, "CDF", "FrCD"],
      // CHE
      "CHE": [ "CHE", "CHE", 130, "CHE", "CHE"],
      // CHF
      "CHF": [ "CHF", "CHF", 2, "CHF", "CHF"],
      // CHW
      "CHW": [ "CHW", "CHW", 130, "CHW", "CHW"],
      // CLE
      "CLE": [ "CLE", "CLE", 130, "CLE", "CLE"],
      // CLF
      "CLF": [ "CLF", "CLF", 132, "CLF", "CLF"],
      // CLP
      "CLP": [ "CLP", "CL$", 0, "CL$", "$"],
      // CNH
      "CNH": [ "CNH", "CNH", 130, "CNH", "CNH"],
      // CNX
      "CNX": [ "CNX", "CNX", 130, "CNX", "CNX"],
      // CNY
      "CNY": [ "CNY", "CN¥", 2, "RMB¥", "¥"],
      // COP
      "COP": [ "COP", "COL$", 2, "COL$", "$"],
      // COU
      "COU": [ "COU", "COU", 130, "COU", "COU"],
      // CRC
      "CRC": [ "CRC", "CR₡", 2, "CR₡", "₡"],
      // CSD
      "CSD": [ "CSD", "CSD", 130, "CSD", "CSD"],
      // CSK
      "CSK": [ "CSK", "CSK", 130, "CSK", "CSK"],
      // CUC
      "CUC": [ "CUC", "CUC", 2, "CUC", "$"],
      // CUP
      "CUP": [ "CUP", "$MN", 2, "$MN", "$"],
      // CVE
      "CVE": [ "CVE", "CVE", 2, "CVE", "CVE"],
      // CYP
      "CYP": [ "CYP", "CYP", 130, "CYP", "CYP"],
      // CZK
      "CZK": [ "CZK", "Kč", 2, "Kč", "Kč"],
      // DDM
      "DDM": [ "DDM", "DDM", 130, "DDM", "DDM"],
      // DEM
      "DEM": [ "DEM", "DEM", 130, "DEM", "DEM"],
      // DJF
      "DJF": [ "DJF", "Fdj", 0, "Fdj", "Fdj"],
      // DKK
      "DKK": [ "DKK", "kr", 2, "kr", "kr"],
      // DOP
      "DOP": [ "DOP", "RD$", 2, "RD$", "$"],
      // DZD
      "DZD": [ "DZD", "DZD", 2, "DZD", "din"],
      // ECS
      "ECS": [ "ECS", "ECS", 130, "ECS", "ECS"],
      // ECV
      "ECV": [ "ECV", "ECV", 130, "ECV", "ECV"],
      // EEK
      "EEK": [ "EEK", "EEK", 130, "EEK", "EEK"],
      // EGP
      "EGP": [ "EGP", "LE", 2, "LE", "E£"],
      // ERN
      "ERN": [ "ERN", "ERN", 2, "ERN", "Nfk"],
      // ESA
      "ESA": [ "ESA", "ESA", 130, "ESA", "ESA"],
      // ESB
      "ESB": [ "ESB", "ESB", 130, "ESB", "ESB"],
      // ESP
      "ESP": [ "ESP", "ESP", 128, "ESP", "ESP"],
      // ETB
      "ETB": [ "ETB", "ETB", 2, "ETB", "Birr"],
      // EUR
      "EUR": [ "EUR", "€", 2, "€", "€"],
      // FIM
      "FIM": [ "FIM", "FIM", 130, "FIM", "FIM"],
      // FJD
      "FJD": [ "FJD", "FJD", 2, "FJD", "$"],
      // FKP
      "FKP": [ "FKP", "FKP", 2, "FKP", "£"],
      // FRF
      "FRF": [ "FRF", "FRF", 130, "FRF", "FRF"],
      // GBP
      "GBP": [ "GBP", "£", 2, "GB£", "£"],
      // GEK
      "GEK": [ "GEK", "GEK", 130, "GEK", "GEK"],
      // GEL
      "GEL": [ "GEL", "GEL", 2, "GEL", "GEL"],
      // GHC
      "GHC": [ "GHC", "GHC", 130, "GHC", "GHC"],
      // GHS
      "GHS": [ "GHS", "GHS", 2, "GHS", "GHS"],
      // GIP
      "GIP": [ "GIP", "GIP", 2, "GIP", "£"],
      // GMD
      "GMD": [ "GMD", "GMD", 2, "GMD", "GMD"],
      // GNF
      "GNF": [ "GNF", "GNF", 0, "GNF", "FG"],
      // GNS
      "GNS": [ "GNS", "GNS", 130, "GNS", "GNS"],
      // GQE
      "GQE": [ "GQE", "GQE", 130, "GQE", "GQE"],
      // GRD
      "GRD": [ "GRD", "GRD", 130, "GRD", "GRD"],
      // GTQ
      "GTQ": [ "GTQ", "GTQ", 2, "GTQ", "Q"],
      // GWE
      "GWE": [ "GWE", "GWE", 130, "GWE", "GWE"],
      // GWP
      "GWP": [ "GWP", "GWP", 130, "GWP", "GWP"],
      // GYD
      "GYD": [ "GYD", "GYD", 2, "GYD", "$"],
      // HKD
      "HKD": [ "HKD", "HK$", 2, "HK$", "$"],
      // HNL
      "HNL": [ "HNL", "L", 2, "L", "L"],
      // HRD
      "HRD": [ "HRD", "HRD", 130, "HRD", "HRD"],
      // HRK
      "HRK": [ "HRK", "HRK", 2, "HRK", "kn"],
      // HTG
      "HTG": [ "HTG", "HTG", 2, "HTG", "HTG"],
      // HUF
      "HUF": [ "HUF", "HUF", 2, "HUF", "Ft"],
      // IDR
      "IDR": [ "IDR", "IDR", 2, "IDR", "Rp"],
      // IEP
      "IEP": [ "IEP", "IEP", 130, "IEP", "IEP"],
      // ILP
      "ILP": [ "ILP", "ILP", 130, "ILP", "ILP"],
      // ILR
      "ILR": [ "ILR", "ILR", 130, "ILR", "ILR"],
      // ILS
      "ILS": [ "ILS", "₪", 2, "IL₪", "₪"],
      // INR
      "INR": [ "INR", "₹", 2, "Rs", "₹"],
      // IQD
      "IQD": [ "IQD", "IQD", 0, "IQD", "din"],
      // IRR
      "IRR": [ "IRR", "IRR", 0, "IRR", "Rial"],
      // ISJ
      "ISJ": [ "ISJ", "ISJ", 130, "ISJ", "ISJ"],
      // ISK
      "ISK": [ "ISK", "kr", 0, "kr", "kr"],
      // ITL
      "ITL": [ "ITL", "ITL", 128, "ITL", "ITL"],
      // JMD
      "JMD": [ "JMD", "JA$", 2, "JA$", "$"],
      // JOD
      "JOD": [ "JOD", "JOD", 3, "JOD", "din"],
      // JPY
      "JPY": [ "JPY", "JP¥", 0, "JP¥", "¥"],
      // KES
      "KES": [ "KES", "Ksh", 2, "Ksh", "Ksh"],
      // KGS
      "KGS": [ "KGS", "KGS", 2, "KGS", "KGS"],
      // KHR
      "KHR": [ "KHR", "KHR", 2, "KHR", "Riel"],
      // KMF
      "KMF": [ "KMF", "KMF", 0, "KMF", "CF"],
      // KPW
      "KPW": [ "KPW", "KPW", 0, "KPW", "₩"],
      // KRH
      "KRH": [ "KRH", "KRH", 130, "KRH", "KRH"],
      // KRO
      "KRO": [ "KRO", "KRO", 130, "KRO", "KRO"],
      // KRW
      "KRW": [ "KRW", "₩", 0, "KR₩", "₩"],
      // KWD
      "KWD": [ "KWD", "KWD", 3, "KWD", "din"],
      // KYD
      "KYD": [ "KYD", "KYD", 2, "KYD", "$"],
      // KZT
      "KZT": [ "KZT", "KZT", 2, "KZT", "₸"],
      // LAK
      "LAK": [ "LAK", "LAK", 0, "LAK", "₭"],
      // LBP
      "LBP": [ "LBP", "LBP", 0, "LBP", "L£"],
      // LKR
      "LKR": [ "LKR", "SLRs", 2, "SLRs", "Rs"],
      // LRD
      "LRD": [ "LRD", "LRD", 2, "LRD", "$"],
      // LSL
      "LSL": [ "LSL", "LSL", 2, "LSL", "LSL"],
      // LTL
      "LTL": [ "LTL", "LTL", 130, "LTL", "Lt"],
      // LTT
      "LTT": [ "LTT", "LTT", 130, "LTT", "LTT"],
      // LUC
      "LUC": [ "LUC", "LUC", 130, "LUC", "LUC"],
      // LUF
      "LUF": [ "LUF", "LUF", 128, "LUF", "LUF"],
      // LUL
      "LUL": [ "LUL", "LUL", 130, "LUL", "LUL"],
      // LVL
      "LVL": [ "LVL", "LVL", 130, "LVL", "Ls"],
      // LVR
      "LVR": [ "LVR", "LVR", 130, "LVR", "LVR"],
      // LYD
      "LYD": [ "LYD", "LYD", 3, "LYD", "din"],
      // MAD
      "MAD": [ "MAD", "MAD", 2, "MAD", "MAD"],
      // MAF
      "MAF": [ "MAF", "MAF", 130, "MAF", "MAF"],
      // MCF
      "MCF": [ "MCF", "MCF", 130, "MCF", "MCF"],
      // MDC
      "MDC": [ "MDC", "MDC", 130, "MDC", "MDC"],
      // MDL
      "MDL": [ "MDL", "MDL", 2, "MDL", "MDL"],
      // MGA
      "MGA": [ "MGA", "MGA", 0, "MGA", "Ar"],
      // MGF
      "MGF": [ "MGF", "MGF", 128, "MGF", "MGF"],
      // MKD
      "MKD": [ "MKD", "MKD", 2, "MKD", "din"],
      // MKN
      "MKN": [ "MKN", "MKN", 130, "MKN", "MKN"],
      // MLF
      "MLF": [ "MLF", "MLF", 130, "MLF", "MLF"],
      // MMK
      "MMK": [ "MMK", "MMK", 0, "MMK", "K"],
      // MNT
      "MNT": [ "MNT", "MN₮", 2, "MN₮", "₮"],
      // MOP
      "MOP": [ "MOP", "MOP", 2, "MOP", "MOP"],
      // MRO
      "MRO": [ "MRO", "MRO", 128, "MRO", "MRO"],
      // MRU
      "MRU": [ "MRU", "MRU", 2, "MRU", "MRU"],
      // MTL
      "MTL": [ "MTL", "MTL", 130, "MTL", "MTL"],
      // MTP
      "MTP": [ "MTP", "MTP", 130, "MTP", "MTP"],
      // MUR
      "MUR": [ "MUR", "MUR", 2, "MUR", "Rs"],
      // MVP
      "MVP": [ "MVP", "MVP", 130, "MVP", "MVP"],
      // MVR
      "MVR": [ "MVR", "MVR", 2, "MVR", "MVR"],
      // MWK
      "MWK": [ "MWK", "MWK", 2, "MWK", "MWK"],
      // MXN
      "MXN": [ "MXN", "MX$", 2, "Mex$", "$"],
      // MXP
      "MXP": [ "MXP", "MXP", 130, "MXP", "MXP"],
      // MXV
      "MXV": [ "MXV", "MXV", 130, "MXV", "MXV"],
      // MYR
      "MYR": [ "MYR", "RM", 2, "RM", "RM"],
      // MZE
      "MZE": [ "MZE", "MZE", 130, "MZE", "MZE"],
      // MZM
      "MZM": [ "MZM", "MZM", 130, "MZM", "MZM"],
      // MZN
      "MZN": [ "MZN", "MZN", 2, "MZN", "MTn"],
      // NAD
      "NAD": [ "NAD", "NAD", 2, "NAD", "$"],
      // NGN
      "NGN": [ "NGN", "NGN", 2, "NGN", "₦"],
      // NIC
      "NIC": [ "NIC", "NIC", 130, "NIC", "NIC"],
      // NIO
      "NIO": [ "NIO", "NIO", 2, "NIO", "C$"],
      // NLG
      "NLG": [ "NLG", "NLG", 130, "NLG", "NLG"],
      // NOK
      "NOK": [ "NOK", "NOkr", 2, "NOkr", "kr"],
      // NPR
      "NPR": [ "NPR", "NPR", 2, "NPR", "Rs"],
      // NZD
      "NZD": [ "NZD", "NZ$", 2, "NZ$", "$"],
      // OMR
      "OMR": [ "OMR", "OMR", 3, "OMR", "Rial"],
      // PAB
      "PAB": [ "PAB", "B/.", 2, "B/.", "B/."],
      // PEI
      "PEI": [ "PEI", "PEI", 130, "PEI", "PEI"],
      // PEN
      "PEN": [ "PEN", "S/.", 2, "S/.", "S/."],
      // PES
      "PES": [ "PES", "PES", 130, "PES", "PES"],
      // PGK
      "PGK": [ "PGK", "PGK", 2, "PGK", "PGK"],
      // PHP
      "PHP": [ "PHP", "PHP", 2, "PHP", "₱"],
      // PKR
      "PKR": [ "PKR", "PKRs.", 2, "PKRs.", "Rs"],
      // PLN
      "PLN": [ "PLN", "PLN", 2, "PLN", "zł"],
      // PLZ
      "PLZ": [ "PLZ", "PLZ", 130, "PLZ", "PLZ"],
      // PTE
      "PTE": [ "PTE", "PTE", 130, "PTE", "PTE"],
      // PYG
      "PYG": [ "PYG", "PYG", 0, "PYG", "Gs"],
      // QAR
      "QAR": [ "QAR", "QAR", 2, "QAR", "Rial"],
      // RHD
      "RHD": [ "RHD", "RHD", 130, "RHD", "RHD"],
      // ROL
      "ROL": [ "ROL", "ROL", 130, "ROL", "ROL"],
      // RON
      "RON": [ "RON", "RON", 2, "RON", "RON"],
      // RSD
      "RSD": [ "RSD", "RSD", 0, "RSD", "din"],
      // RUB
      "RUB": [ "RUB", "руб.", 2, "руб.", "руб."],
      // RUR
      "RUR": [ "RUR", "RUR", 130, "RUR", "RUR"],
      // RWF
      "RWF": [ "RWF", "RWF", 0, "RWF", "RF"],
      // SAR
      "SAR": [ "SAR", "SR", 2, "SR", "Rial"],
      // SBD
      "SBD": [ "SBD", "SBD", 2, "SBD", "$"],
      // SCR
      "SCR": [ "SCR", "SCR", 2, "SCR", "SCR"],
      // SDD
      "SDD": [ "SDD", "SDD", 130, "SDD", "SDD"],
      // SDG
      "SDG": [ "SDG", "SDG", 2, "SDG", "SDG"],
      // SDP
      "SDP": [ "SDP", "SDP", 130, "SDP", "SDP"],
      // SEK
      "SEK": [ "SEK", "kr", 2, "kr", "kr"],
      // SGD
      "SGD": [ "SGD", "S$", 2, "S$", "$"],
      // SHP
      "SHP": [ "SHP", "SHP", 2, "SHP", "£"],
      // SIT
      "SIT": [ "SIT", "SIT", 130, "SIT", "SIT"],
      // SKK
      "SKK": [ "SKK", "SKK", 130, "SKK", "SKK"],
      // SLL
      "SLL": [ "SLL", "SLL", 0, "SLL", "SLL"],
      // SOS
      "SOS": [ "SOS", "SOS", 0, "SOS", "SOS"],
      // SRD
      "SRD": [ "SRD", "SRD", 2, "SRD", "$"],
      // SRG
      "SRG": [ "SRG", "SRG", 130, "SRG", "SRG"],
      // SSP
      "SSP": [ "SSP", "SSP", 2, "SSP", "SSP"],
      // STD
      "STD": [ "STD", "STD", 128, "STD", "Db"],
      // STN
      "STN": [ "STN", "STN", 2, "STN", "STN"],
      // SUR
      "SUR": [ "SUR", "SUR", 130, "SUR", "SUR"],
      // SVC
      "SVC": [ "SVC", "SVC", 130, "SVC", "SVC"],
      // SYP
      "SYP": [ "SYP", "SYP", 0, "SYP", "£"],
      // SZL
      "SZL": [ "SZL", "SZL", 2, "SZL", "SZL"],
      // THB
      "THB": [ "THB", "THB", 2, "THB", "฿"],
      // TJR
      "TJR": [ "TJR", "TJR", 130, "TJR", "TJR"],
      // TJS
      "TJS": [ "TJS", "TJS", 2, "TJS", "Som"],
      // TMM
      "TMM": [ "TMM", "TMM", 128, "TMM", "TMM"],
      // TMT
      "TMT": [ "TMT", "TMT", 2, "TMT", "TMT"],
      // TND
      "TND": [ "TND", "TND", 3, "TND", "din"],
      // TOP
      "TOP": [ "TOP", "TOP", 2, "TOP", "T$"],
      // TPE
      "TPE": [ "TPE", "TPE", 130, "TPE", "TPE"],
      // TRL
      "TRL": [ "TRL", "TRL", 128, "TRL", "TRL"],
      // TRY
      "TRY": [ "TRY", "TL", 2, "YTL", "TL"],
      // TTD
      "TTD": [ "TTD", "TTD", 2, "TTD", "$"],
      // TWD
      "TWD": [ "TWD", "NT$", 2, "NT$", "NT$"],
      // TZS
      "TZS": [ "TZS", "TZS", 2, "TZS", "TSh"],
      // UAH
      "UAH": [ "UAH", "UAH", 2, "UAH", "₴"],
      // UAK
      "UAK": [ "UAK", "UAK", 130, "UAK", "UAK"],
      // UGS
      "UGS": [ "UGS", "UGS", 130, "UGS", "UGS"],
      // UGX
      "UGX": [ "UGX", "UGX", 0, "UGX", "UGX"],
      // USD
      "USD": [ "USD", "US$", 2, "US$", "$"],
      // USN
      "USN": [ "USN", "USN", 130, "USN", "USN"],
      // USS
      "USS": [ "USS", "USS", 130, "USS", "USS"],
      // UYI
      "UYI": [ "UYI", "UYI", 128, "UYI", "UYI"],
      // UYP
      "UYP": [ "UYP", "UYP", 130, "UYP", "UYP"],
      // UYU
      "UYU": [ "UYU", "UY$", 2, "UY$", "$"],
      // UYW
      "UYW": [ "UYW", "UYW", 132, "UYW", "UYW"],
      // UZS
      "UZS": [ "UZS", "UZS", 2, "UZS", "soʼm"],
      // VEB
      "VEB": [ "VEB", "VEB", 130, "VEB", "VEB"],
      // VEF
      "VEF": [ "VEF", "VEF", 130, "VEF", "Bs"],
      // VES
      "VES": [ "VES", "VES", 2, "VES", "VES"],
      // VND
      "VND": [ "VND", "₫", 24, "₫", "₫"],
      // VNN
      "VNN": [ "VNN", "VNN", 130, "VNN", "VNN"],
      // VUV
      "VUV": [ "VUV", "VUV", 0, "VUV", "VUV"],
      // WST
      "WST": [ "WST", "WST", 2, "WST", "WST"],
      // XAF
      "XAF": [ "XAF", "FCFA", 0, "FCFA", "FCFA"],
      // XAG
      "XAG": [ "XAG", "XAG", 130, "XAG", "XAG"],
      // XAU
      "XAU": [ "XAU", "XAU", 130, "XAU", "XAU"],
      // XBA
      "XBA": [ "XBA", "XBA", 130, "XBA", "XBA"],
      // XBB
      "XBB": [ "XBB", "XBB", 130, "XBB", "XBB"],
      // XBC
      "XBC": [ "XBC", "XBC", 130, "XBC", "XBC"],
      // XBD
      "XBD": [ "XBD", "XBD", 130, "XBD", "XBD"],
      // XCD
      "XCD": [ "XCD", "EC$", 2, "EC$", "$"],
      // XDR
      "XDR": [ "XDR", "XDR", 130, "XDR", "XDR"],
      // XEU
      "XEU": [ "XEU", "XEU", 130, "XEU", "XEU"],
      // XFO
      "XFO": [ "XFO", "XFO", 130, "XFO", "XFO"],
      // XFU
      "XFU": [ "XFU", "XFU", 130, "XFU", "XFU"],
      // XOF
      "XOF": [ "XOF", "CFA", 0, "CFA", "CFA"],
      // XPD
      "XPD": [ "XPD", "XPD", 130, "XPD", "XPD"],
      // XPF
      "XPF": [ "XPF", "CFPF", 0, "CFPF", "FCFP"],
      // XPT
      "XPT": [ "XPT", "XPT", 130, "XPT", "XPT"],
      // XRE
      "XRE": [ "XRE", "XRE", 130, "XRE", "XRE"],
      // XSU
      "XSU": [ "XSU", "XSU", 130, "XSU", "XSU"],
      // XTS
      "XTS": [ "XTS", "XTS", 130, "XTS", "XTS"],
      // XUA
      "XUA": [ "XUA", "XUA", 130, "XUA", "XUA"],
      // XXX
      "XXX": [ "XXX", "¤", 130, "¤", "¤"],
      // YDD
      "YDD": [ "YDD", "YDD", 130, "YDD", "YDD"],
      // YER
      "YER": [ "YER", "YER", 0, "YER", "Rial"],
      // YUD
      "YUD": [ "YUD", "YUD", 130, "YUD", "YUD"],
      // YUM
      "YUM": [ "YUM", "YUM", 130, "YUM", "YUM"],
      // YUN
      "YUN": [ "YUN", "YUN", 130, "YUN", "YUN"],
      // YUR
      "YUR": [ "YUR", "YUR", 130, "YUR", "YUR"],
      // ZAL
      "ZAL": [ "ZAL", "ZAL", 130, "ZAL", "ZAL"],
      // ZAR
      "ZAR": [ "ZAR", "ZAR", 2, "ZAR", "R"],
      // ZMK
      "ZMK": [ "ZMK", "ZMK", 128, "ZMK", "ZWK"],
      // ZMW
      "ZMW": [ "ZMW", "ZMW", 2, "ZMW", "ZMW"],
      // ZRN
      "ZRN": [ "ZRN", "ZRN", 130, "ZRN", "ZRN"],
      // ZRZ
      "ZRZ": [ "ZRZ", "ZRZ", 130, "ZRZ", "ZRZ"],
      // ZWD
      "ZWD": [ "ZWD", "ZWD", 128, "ZWD", "ZWD"],
      // ZWL
      "ZWL": [ "ZWL", "ZWL", 130, "ZWL", "ZWL"],
      // ZWR
      "ZWR": [ "ZWR", "ZWR", 130, "ZWR", "ZWR"],
    };
  }-*/;
}
