package com.youa.mobile.common.util.picture;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;


public class CodeFactory {

	private HashMap<String, String> mCC = new HashMap<String, String>();
	private HashMap<String, String> iSO = new HashMap<String, String>();
	private HashSet<String> cCC = new HashSet<String>();
	private ArrayList<CountryFilterBean> countryMap = new ArrayList<CountryFilterBean>();

	public CodeFactory() {
		initCCC();
		initMCC();
		initISO();
		initCountryMap();
	}


	public ArrayList<CountryBean> countryFilter(String core) {
		ArrayList<CountryBean> resultCountries = new ArrayList<CountryBean>();
		if (core == null || core.length() < 1) {
			resultCountries.addAll(countryMap);
			return resultCountries;
		}
		core = core.toLowerCase();
		for (CountryFilterBean countryFilterBean : countryMap) {
			if (countryFilterBean.getKey().contains(core)) {
				resultCountries.add(countryFilterBean);
			}
		}
		return resultCountries;
	}


	public String getCCCByDeviceInfo(String oriPhone, String simMCC,
			String simISO, String netMCC, String netISO) {
		return formatCodeByCCC(oriPhone,
				getCCCByMCCOrISO(simMCC, simISO, netMCC, netISO)).getZcode();
	}


	public String getCCCByMCCOrISO(String simMCC, String simISO, String netMCC,
			String netISO) {
		String cCC = mCC2CCC(simMCC);
		if (cCC != null)
			return cCC;
		cCC = iSO2CCC(simISO);
		if (cCC != null)
			return cCC;
		cCC = mCC2CCC(netMCC);
		if (cCC != null)
			return cCC;
		cCC = iSO2CCC(netISO);
		return cCC;
	}


	public CodeBean formatCodeByMCC(String oriPhone, String mcc) {
		return formatCodeByCCC(oriPhone, mCC2CCC(mcc));
	}


	public CodeBean formatCodeByISO(String oriPhone, String iso) {
		return formatCodeByCCC(oriPhone, iSO2CCC(iso));
	}
	


	public CodeBean formatCodeByCCC(String oriPhone, String ccc) {
		CodeBean codeBean = new CodeBean();
		if (ccc == null || !checkCCC(ccc)) {
			ccc = null;
			codeBean.setZcode("1");//默认
		} else {
			codeBean.setZcode(ccc);
		}
		

		// '+'->'00', clear non-numeric. eg: +86x9-0 -> 008690
		oriPhone = clearNonNumeric(oriPhone); // length>2, otherwise=null

		// a. no phone
		if (oriPhone == null) {
			return codeBean;
		}

		// b. 18610104684, ccc
		if (!oriPhone.substring(0, 2).equalsIgnoreCase("00")) {
			codeBean.setPhone(oriPhone);
			if (ccc != null) {
				codeBean.setCorrect(true);
				if ("86".equalsIgnoreCase(ccc) && oriPhone.length() > 5) {
					if ("12580".equalsIgnoreCase(oriPhone.substring(0, 5))) {
						codeBean.setPhone(oriPhone.substring(5));
					}
				}
			}
			return codeBean;
		}

		oriPhone = oriPhone.substring(2);
		// c. +0086-18610104684 or 000086-18678763 ...
		if (oriPhone.length() > 1
				&& oriPhone.substring(0, 2).equalsIgnoreCase("00")) {
			return formatCodeByCCC(oriPhone, ccc);
		}

		// d. +86-1
		if (oriPhone.length() < 4) {
			return codeBean;
		}

		// e. +86-18610104684, 86
		if (ccc != null
				&& oriPhone.substring(0, ccc.length()).equalsIgnoreCase(ccc)) {
			codeBean.setPhone(oriPhone.substring(ccc.length()));
			codeBean.setCorrect(true);
			return codeBean;
		}
		// f. +811-18610104684, ccc
		String oriCCC = oriPhone.substring(0, 3);
		if (checkCCC(oriCCC)) {
			codeBean.setZcode(oriCCC);
			codeBean.setPhone(oriPhone.substring(3));
			return codeBean;
		}
		// g. +86-18610104684, ccc
		oriCCC = oriPhone.substring(0, 2);
		if (checkCCC(oriCCC)) {
			codeBean.setZcode(oriCCC);
			codeBean.setPhone(oriPhone.substring(2));
			return codeBean;
		}
		// h. +1-18610104684, ccc
		oriCCC = oriPhone.substring(0, 1);
		if (checkCCC(oriCCC)) {
			codeBean.setZcode(oriCCC);
			codeBean.setPhone(oriPhone.substring(1));
			return codeBean;
		}
		// i. failed
		return codeBean;
	}

	/**
	 * only for server, level 2
	 * 
	 * @param ccc
	 * @return
	 */
	public boolean checkCCC(String ccc) {
		return cCC.contains(ccc);
	}

	/**
	 * @param oriPhone
	 * @return "+"->"00", clear non-numeric. null both while source is null or
	 *         empty , and while result is shorter than 3 .
	 */
	private String clearNonNumeric(String oriPhone) {
		if (oriPhone == null || oriPhone.length() < 1) {
			return null;
		}
		if (oriPhone.charAt(0) == '+') {
			oriPhone = "00" + oriPhone.substring(1);
		}

		oriPhone = oriPhone.replaceAll("(\\D)", "");
		if (oriPhone.length() < 3) {
			return null;
		}
		return oriPhone;
	}

	/**
	 * @param mcc
	 *            3 numbers
	 * @return ccc
	 */
	private String mCC2CCC(String mcc) {
		return mCC.get(mcc);
	}

	/**
	 * @param iso
	 *            2 roman characters
	 * @return ccc
	 */
	private String iSO2CCC(String iso) {
		if (iso == null) {
			return null;
		}
		iso = iso.toUpperCase();
		if (iso.equals("SX")) {
			if (useNewZcodeInSX()) {
				return "1"; // before 2011-9-30
			}
			return "599";
		}
		return iSO.get(iso);
	}

	/**
	 * SX will use 1 after 2011-9-30, before use 599
	 * 
	 * @return true use 1,false 599
	 */
	private boolean useNewZcodeInSX() {
		Date date = new Date(2011, 9, 30);
		Date now = new Date();
		if (now.getTime() < date.getTime()) {
			return false;
		}
		return true;
	}

	private void initCCC() {
		cCC.add("1");
		cCC.add("7");
		cCC.add("20");
		cCC.add("27");
		cCC.add("28");
		cCC.add("30");
		cCC.add("31");
		cCC.add("32");
		cCC.add("33");
		cCC.add("34");
		cCC.add("36");
		cCC.add("39");
		cCC.add("40");
		cCC.add("41");
		cCC.add("43");
		cCC.add("44");
		cCC.add("45");
		cCC.add("46");
		cCC.add("47");
		cCC.add("48");
		cCC.add("49");
		cCC.add("51");
		cCC.add("52");
		cCC.add("53");
		cCC.add("54");
		cCC.add("55");
		cCC.add("56");
		cCC.add("57");
		cCC.add("58");
		cCC.add("60");
		cCC.add("61");
		cCC.add("62");
		cCC.add("63");
		cCC.add("64");
		cCC.add("65");
		cCC.add("66");
		cCC.add("81");
		cCC.add("82");
		cCC.add("83");
		cCC.add("84");
		cCC.add("86");
		cCC.add("89");
		cCC.add("90");
		cCC.add("91");
		cCC.add("92");
		cCC.add("93");
		cCC.add("94");
		cCC.add("95");
		cCC.add("98");
		cCC.add("210");
		cCC.add("211");
		cCC.add("212");
		cCC.add("213");
		cCC.add("214");
		cCC.add("215");
		cCC.add("216");
		cCC.add("217");
		cCC.add("218");
		cCC.add("219");
		cCC.add("220");
		cCC.add("221");
		cCC.add("222");
		cCC.add("223");
		cCC.add("224");
		cCC.add("225");
		cCC.add("226");
		cCC.add("227");
		cCC.add("228");
		cCC.add("229");
		cCC.add("230");
		cCC.add("231");
		cCC.add("232");
		cCC.add("233");
		cCC.add("234");
		cCC.add("235");
		cCC.add("236");
		cCC.add("237");
		cCC.add("238");
		cCC.add("239");
		cCC.add("240");
		cCC.add("241");
		cCC.add("242");
		cCC.add("243");
		cCC.add("244");
		cCC.add("245");
		cCC.add("246");
		cCC.add("247");
		cCC.add("248");
		cCC.add("249");
		cCC.add("250");
		cCC.add("251");
		cCC.add("252");
		cCC.add("253");
		cCC.add("254");
		cCC.add("255");
		cCC.add("256");
		cCC.add("257");
		cCC.add("258");
		cCC.add("259");
		cCC.add("260");
		cCC.add("261");
		cCC.add("262");
		cCC.add("263");
		cCC.add("264");
		cCC.add("265");
		cCC.add("266");
		cCC.add("267");
		cCC.add("268");
		cCC.add("269");
		cCC.add("290");
		cCC.add("291");
		cCC.add("292");
		cCC.add("293");
		cCC.add("294");
		cCC.add("295");
		cCC.add("296");
		cCC.add("297");
		cCC.add("298");
		cCC.add("299");
		cCC.add("350");
		cCC.add("351");
		cCC.add("352");
		cCC.add("353");
		cCC.add("354");
		cCC.add("355");
		cCC.add("356");
		cCC.add("357");
		cCC.add("358");
		cCC.add("359");
		cCC.add("370");
		cCC.add("371");
		cCC.add("372");
		cCC.add("373");
		cCC.add("374");
		cCC.add("375");
		cCC.add("376");
		cCC.add("377");
		cCC.add("378");
		cCC.add("379");
		cCC.add("380");
		cCC.add("381");
		cCC.add("382");
		cCC.add("383");
		cCC.add("384");
		cCC.add("385");
		cCC.add("386");
		cCC.add("387");
		cCC.add("388");
		cCC.add("389");
		cCC.add("420");
		cCC.add("421");
		cCC.add("422");
		cCC.add("423");
		cCC.add("424");
		cCC.add("425");
		cCC.add("426");
		cCC.add("427");
		cCC.add("428");
		cCC.add("429");
		cCC.add("500");
		cCC.add("501");
		cCC.add("502");
		cCC.add("503");
		cCC.add("504");
		cCC.add("505");
		cCC.add("506");
		cCC.add("507");
		cCC.add("508");
		cCC.add("509");
		cCC.add("590");
		cCC.add("591");
		cCC.add("592");
		cCC.add("593");
		cCC.add("594");
		cCC.add("595");
		cCC.add("596");
		cCC.add("597");
		cCC.add("598");
		cCC.add("599");
		cCC.add("670");
		cCC.add("671");
		cCC.add("672");
		cCC.add("673");
		cCC.add("674");
		cCC.add("675");
		cCC.add("676");
		cCC.add("677");
		cCC.add("678");
		cCC.add("679");
		cCC.add("680");
		cCC.add("681");
		cCC.add("682");
		cCC.add("683");
		cCC.add("684");
		cCC.add("685");
		cCC.add("686");
		cCC.add("687");
		cCC.add("688");
		cCC.add("689");
		cCC.add("690");
		cCC.add("691");
		cCC.add("692");
		cCC.add("693");
		cCC.add("694");
		cCC.add("695");
		cCC.add("696");
		cCC.add("697");
		cCC.add("698");
		cCC.add("699");
		cCC.add("800");
		cCC.add("801");
		cCC.add("802");
		cCC.add("803");
		cCC.add("804");
		cCC.add("805");
		cCC.add("806");
		cCC.add("807");
		cCC.add("808");
		cCC.add("809");
		cCC.add("850");
		cCC.add("851");
		cCC.add("852");
		cCC.add("853");
		cCC.add("854");
		cCC.add("855");
		cCC.add("856");
		cCC.add("857");
		cCC.add("858");
		cCC.add("859");
		cCC.add("870");
		cCC.add("871");
		cCC.add("872");
		cCC.add("873");
		cCC.add("874");
		cCC.add("875");
		cCC.add("876");
		cCC.add("877");
		cCC.add("878");
		cCC.add("879");
		cCC.add("880");
		cCC.add("881");
		cCC.add("882");
		cCC.add("883");
		cCC.add("884");
		cCC.add("885");
		cCC.add("886");
		cCC.add("887");
		cCC.add("888");
		cCC.add("889");
		cCC.add("960");
		cCC.add("961");
		cCC.add("962");
		cCC.add("963");
		cCC.add("964");
		cCC.add("965");
		cCC.add("966");
		cCC.add("967");
		cCC.add("968");
		cCC.add("969");
		cCC.add("970");
		cCC.add("971");
		cCC.add("972");
		cCC.add("973");
		cCC.add("974");
		cCC.add("975");
		cCC.add("976");
		cCC.add("977");
		cCC.add("978");
		cCC.add("979");
		cCC.add("990");
		cCC.add("991");
		cCC.add("992");
		cCC.add("993");
		cCC.add("994");
		cCC.add("995");
		cCC.add("996");
		cCC.add("997");
		cCC.add("998");
		cCC.add("999");
	}

	private void initMCC() {
		mCC.put("200", "1");
		mCC.put("202", "30");
		mCC.put("204", "31");
		mCC.put("206", "32");
		mCC.put("208", "33");
		mCC.put("212", "377");
		mCC.put("213", "376");
		mCC.put("214", "34");
		mCC.put("216", "36");
		mCC.put("218", "387");
		mCC.put("219", "385");
		mCC.put("220", "381");
		mCC.put("222", "39");
		mCC.put("225", "379");
		mCC.put("226", "40");
		mCC.put("228", "41");
		mCC.put("230", "420");
		mCC.put("231", "421");
		mCC.put("232", "43");
		mCC.put("234", "44");
		mCC.put("235", "44");
		mCC.put("238", "45");
		mCC.put("240", "46");
		mCC.put("242", "47");
		mCC.put("244", "358");
		mCC.put("246", "370");
		mCC.put("247", "371");
		mCC.put("248", "372");
		mCC.put("250", "7");
		mCC.put("255", "380");
		mCC.put("257", "375");
		mCC.put("259", "373");
		mCC.put("260", "48");
		mCC.put("262", "49");
		mCC.put("266", "350");
		mCC.put("268", "351");
		mCC.put("270", "352");
		mCC.put("272", "353");
		mCC.put("274", "354");
		mCC.put("276", "355");
		mCC.put("278", "356");
		mCC.put("280", "357");
		mCC.put("282", "995");
		mCC.put("283", "374");
		mCC.put("284", "359");
		mCC.put("286", "90");
		mCC.put("288", "298");
		mCC.put("289", "7");
		mCC.put("290", "299");
		mCC.put("292", "378");
		mCC.put("293", "386");
		mCC.put("294", "389");
		mCC.put("295", "423");
		mCC.put("297", "382");
		mCC.put("302", "1");
		mCC.put("308", "508");
		mCC.put("310", "1");
		mCC.put("311", "1");
		mCC.put("312", "1");
		mCC.put("313", "1");
		mCC.put("314", "1");
		mCC.put("315", "1");
		mCC.put("316", "1");
		mCC.put("330", "1");
		mCC.put("332", "1");
		mCC.put("334", "52");
		mCC.put("338", "1");
		mCC.put("340", "590");
		mCC.put("342", "1");
		mCC.put("344", "1");
		mCC.put("346", "1");
		mCC.put("348", "1");
		mCC.put("350", "1");
		mCC.put("352", "1");
		mCC.put("354", "1");
		mCC.put("356", "1");
		mCC.put("358", "1");
		mCC.put("360", "1");
		mCC.put("362", "599");
		mCC.put("363", "297");
		mCC.put("364", "1");
		mCC.put("365", "1");
		mCC.put("366", "1");
		mCC.put("368", "53");
		mCC.put("370", "1");
		mCC.put("372", "509");
		mCC.put("374", "1");
		mCC.put("376", "1");
		mCC.put("400", "994");
		mCC.put("401", "7");
		mCC.put("402", "975");
		mCC.put("404", "91");
		mCC.put("405", "91");
		mCC.put("406", "91");
		mCC.put("410", "92");
		mCC.put("412", "93");
		mCC.put("413", "94");
		mCC.put("414", "95");
		mCC.put("415", "961");
		mCC.put("416", "962");
		mCC.put("417", "963");
		mCC.put("418", "964");
		mCC.put("419", "965");
		mCC.put("420", "966");
		mCC.put("421", "967");
		mCC.put("422", "968");
		mCC.put("423", "970");
		mCC.put("424", "971");
		mCC.put("425", "972");
		mCC.put("426", "973");
		mCC.put("427", "974");
		mCC.put("428", "976");
		mCC.put("429", "977");
		mCC.put("432", "98");
		mCC.put("434", "998");
		mCC.put("436", "992");
		mCC.put("437", "996");
		mCC.put("438", "993");
		mCC.put("440", "81");
		mCC.put("441", "81");
		mCC.put("450", "82");
		mCC.put("452", "84");
		mCC.put("454", "852");
		mCC.put("455", "853");
		mCC.put("456", "855");
		mCC.put("457", "856");
		mCC.put("460", "86");
		mCC.put("461", "86");
		mCC.put("466", "886");
		mCC.put("467", "850");
		mCC.put("470", "880");
		mCC.put("472", "960");
		mCC.put("502", "60");
		mCC.put("505", "61");
		mCC.put("510", "62");
		mCC.put("514", "670");
		mCC.put("515", "63");
		mCC.put("520", "66");
		mCC.put("525", "65");
		mCC.put("528", "673");
		mCC.put("530", "64");
		mCC.put("534", "1");
		mCC.put("535", "1");
		mCC.put("536", "674");
		mCC.put("537", "675");
		mCC.put("539", "676");
		mCC.put("540", "677");
		mCC.put("541", "678");
		mCC.put("542", "679");
		mCC.put("543", "681");
		mCC.put("544", "1");
		mCC.put("545", "686");
		mCC.put("546", "687");
		mCC.put("547", "689");
		mCC.put("548", "682");
		mCC.put("549", "685");
		mCC.put("550", "691");
		mCC.put("551", "692");
		mCC.put("552", "680");
		mCC.put("553", "688");
		mCC.put("555", "691");
		mCC.put("602", "20");
		mCC.put("603", "213");
		mCC.put("604", "212");
		mCC.put("605", "216");
		mCC.put("606", "218");
		mCC.put("607", "220");
		mCC.put("608", "221");
		mCC.put("609", "222");
		mCC.put("610", "223");
		mCC.put("611", "224");
		mCC.put("612", "225");
		mCC.put("613", "226");
		mCC.put("614", "227");
		mCC.put("615", "228");
		mCC.put("616", "229");
		mCC.put("617", "230");
		mCC.put("618", "231");
		mCC.put("619", "232");
		mCC.put("620", "233");
		mCC.put("621", "234");
		mCC.put("622", "235");
		mCC.put("623", "236");
		mCC.put("624", "237");
		mCC.put("625", "238");
		mCC.put("626", "239");
		mCC.put("627", "240");
		mCC.put("628", "241");
		mCC.put("629", "242");
		mCC.put("630", "243");
		mCC.put("631", "244");
		mCC.put("632", "245");
		mCC.put("633", "248");
		mCC.put("634", "249");
		mCC.put("635", "250");
		mCC.put("636", "251");
		mCC.put("637", "252");
		mCC.put("638", "253");
		mCC.put("639", "254");
		mCC.put("640", "255");
		mCC.put("641", "256");
		mCC.put("642", "257");
		mCC.put("643", "258");
		mCC.put("645", "260");
		mCC.put("646", "261");
		mCC.put("647", "262");
		mCC.put("648", "263");
		mCC.put("649", "264");
		mCC.put("650", "265");
		mCC.put("651", "266");
		mCC.put("652", "267");
		mCC.put("653", "268");
		mCC.put("654", "269");
		mCC.put("655", "27");
		mCC.put("657", "291");
		mCC.put("702", "501");
		mCC.put("704", "502");
		mCC.put("706", "503");
		mCC.put("708", "504");
		mCC.put("710", "505");
		mCC.put("712", "506");
		mCC.put("714", "507");
		mCC.put("716", "51");
		mCC.put("722", "54");
		mCC.put("724", "55");
		mCC.put("730", "56");
		mCC.put("732", "57");
		mCC.put("734", "58");
		mCC.put("736", "591");
		mCC.put("738", "592");
		mCC.put("740", "593");
		mCC.put("742", "594");
		mCC.put("744", "595");
		mCC.put("746", "597");
		mCC.put("748", "598");
		mCC.put("750", "500");
	}

	private void initISO() {
		iSO.put("AC", "247");
		iSO.put("AD", "376");
		iSO.put("AE", "971");
		iSO.put("AF", "93");
		iSO.put("AG", "1");
		iSO.put("AI", "1");
		iSO.put("AL", "355");
		iSO.put("AM", "374");
		iSO.put("AN", "599");
		iSO.put("AO", "244");
		iSO.put("AQ", "672");
		iSO.put("AR", "54");
		iSO.put("AS", "1");
		iSO.put("AT", "43");
		iSO.put("AU", "61");
		iSO.put("AW", "297");
		iSO.put("AX", "358");
		iSO.put("AZ", "994");
		iSO.put("BA", "387");
		iSO.put("BB", "1");
		iSO.put("BD", "880");
		iSO.put("BE", "32");
		iSO.put("BF", "226");
		iSO.put("BG", "359");
		iSO.put("BH", "973");
		iSO.put("BI", "257");
		iSO.put("BJ", "229");
		iSO.put("BL", "590");
		iSO.put("BM", "1");
		iSO.put("BN", "673");
		iSO.put("BO", "591");
		iSO.put("BQ", "599");
		iSO.put("BR", "55");
		iSO.put("BS", "1");
		iSO.put("BT", "975");
		iSO.put("BW", "267");
		iSO.put("BY", "375");
		iSO.put("BZ", "501");
		iSO.put("CA", "1");
		iSO.put("CC", "61");
		iSO.put("CD", "243");
		iSO.put("CF", "236");
		iSO.put("CG", "242");
		iSO.put("CH", "41");
		iSO.put("CI", "225");
		iSO.put("CK", "682");
		iSO.put("CL", "56");
		iSO.put("CM", "237");
		iSO.put("CN", "86");
		iSO.put("CO", "57");
		iSO.put("CR", "506");
		iSO.put("CU", "53");
		iSO.put("CV", "238");
		iSO.put("CW", "599");
		iSO.put("CX", "61");
		iSO.put("CY", "357");
		iSO.put("CZ", "420");
		iSO.put("DE", "49");
		iSO.put("DJ", "253");
		iSO.put("DK", "45");
		iSO.put("DM", "1");
		iSO.put("DO", "1");
		iSO.put("DZ", "213");
		iSO.put("EC", "593");
		iSO.put("EE", "372");
		iSO.put("EG", "20");
		iSO.put("EH", "212");
		iSO.put("ER", "291");
		iSO.put("ES", "34");
		iSO.put("ET", "251");
		iSO.put("EU", "388");
		iSO.put("FI", "358");
		iSO.put("FJ", "679");
		iSO.put("FK", "500");
		iSO.put("FM", "691");
		iSO.put("FO", "298");
		iSO.put("FR", "33");
		iSO.put("GA", "241");
		iSO.put("GB", "44");
		iSO.put("GD", "1");
		iSO.put("GE", "995");
		iSO.put("GF", "594");
		iSO.put("GG", "44");
		iSO.put("GH", "233");
		iSO.put("GI", "350");
		iSO.put("GL", "299");
		iSO.put("GM", "220");
		iSO.put("GN", "224");
		iSO.put("GP", "590");
		iSO.put("GQ", "240");
		iSO.put("GR", "30");
		iSO.put("GT", "502");
		iSO.put("GU", "1");
		iSO.put("GW", "245");
		iSO.put("GY", "592");
		iSO.put("HK", "852");
		iSO.put("HN", "504");
		iSO.put("HR", "385");
		iSO.put("HT", "509");
		iSO.put("HU", "36");
		iSO.put("ID", "62");
		iSO.put("IE", "353");
		iSO.put("IL", "972");
		iSO.put("IM", "44");
		iSO.put("IN", "91");
		iSO.put("IO", "246");
		iSO.put("IQ", "964");
		iSO.put("IR", "98");
		iSO.put("IS", "354");
		iSO.put("IT", "39");
		iSO.put("JE", "44");
		iSO.put("JM", "1");
		iSO.put("JO", "962");
		iSO.put("JP", "81");
		iSO.put("KE", "254");
		iSO.put("KG", "996");
		iSO.put("KH", "855");
		iSO.put("KI", "686");
		iSO.put("KM", "269");
		iSO.put("KN", "1");
		iSO.put("KP", "850");
		iSO.put("KR", "82");
		iSO.put("KW", "965");
		iSO.put("KY", "1");
		iSO.put("KZ", "7");
		iSO.put("LA", "856");
		iSO.put("LB", "961");
		iSO.put("LC", "1");
		iSO.put("LI", "423");
		iSO.put("LK", "94");
		iSO.put("LR", "231");
		iSO.put("LS", "266");
		iSO.put("LT", "370");
		iSO.put("LU", "352");
		iSO.put("LV", "371");
		iSO.put("LY", "218");
		iSO.put("MA", "212");
		iSO.put("MC", "377");
		iSO.put("MD", "373");
		iSO.put("ME", "382");
		iSO.put("MF", "590");
		iSO.put("MG", "261");
		iSO.put("MH", "692");
		iSO.put("MK", "389");
		iSO.put("ML", "223");
		iSO.put("MM", "95");
		iSO.put("MN", "976");
		iSO.put("MO", "853");
		iSO.put("MP", "1");
		iSO.put("MQ", "596");
		iSO.put("MR", "222");
		iSO.put("MS", "1");
		iSO.put("MT", "356");
		iSO.put("MU", "230");
		iSO.put("MV", "960");
		iSO.put("MW", "265");
		iSO.put("MX", "52");
		iSO.put("MY", "60");
		iSO.put("MZ", "258");
		iSO.put("NA", "264");
		iSO.put("NC", "687");
		iSO.put("NE", "227");
		iSO.put("NF", "672");
		iSO.put("NG", "234");
		iSO.put("NI", "505");
		iSO.put("NL", "31");
		iSO.put("NO", "47");
		iSO.put("NP", "977");
		iSO.put("NR", "674");
		iSO.put("NU", "683");
		iSO.put("NZ", "64");
		iSO.put("OM", "968");
		iSO.put("PA", "507");
		iSO.put("PE", "51");
		iSO.put("PF", "689");
		iSO.put("PG", "675");
		iSO.put("PH", "63");
		iSO.put("PK", "92");
		iSO.put("PL", "48");
		iSO.put("PM", "508");
		iSO.put("PR", "1");
		iSO.put("PS", "970");
		iSO.put("PT", "351");
		iSO.put("PW", "680");
		iSO.put("PY", "595");
		iSO.put("QA", "974");
		iSO.put("QN", "374");
		iSO.put("QS", "252");
		iSO.put("QY", "90");
		iSO.put("RE", "262");
		iSO.put("RO", "40");
		iSO.put("RS", "381");
		iSO.put("RU", "7");
		iSO.put("RW", "250");
		iSO.put("SA", "966");
		iSO.put("SB", "677");
		iSO.put("SC", "248");
		iSO.put("SD", "249");
		iSO.put("SE", "46");
		iSO.put("SG", "65");
		iSO.put("SH", "290");
		iSO.put("SI", "386");
		iSO.put("SJ", "47");
		iSO.put("SK", "421");
		iSO.put("SL", "232");
		iSO.put("SM", "378");
		iSO.put("SN", "221");
		iSO.put("SO", "252");
		iSO.put("SR", "597");
		iSO.put("ST", "239");
		iSO.put("SV", "503");
		iSO.put("SX", "599");
		iSO.put("SY", "963");
		iSO.put("SZ", "268");
		iSO.put("TA", "290");
		iSO.put("TC", "1");
		iSO.put("TD", "235");
		iSO.put("TG", "228");
		iSO.put("TH", "66");
		iSO.put("TJ", "992");
		iSO.put("TK", "690");
		iSO.put("TL", "670");
		iSO.put("TM", "993");
		iSO.put("TN", "216");
		iSO.put("TO", "676");
		iSO.put("TR", "90");
		iSO.put("TT", "1");
		iSO.put("TV", "688");
		iSO.put("TW", "886");
		iSO.put("TZ", "255");
		iSO.put("UA", "380");
		iSO.put("UG", "256");
		iSO.put("UK", "44");
		iSO.put("US", "1");
		iSO.put("UY", "598");
		iSO.put("UZ", "998");
		iSO.put("VA", "379");
		iSO.put("VC", "1");
		iSO.put("VE", "58");
		iSO.put("VG", "1");
		iSO.put("VI", "1");
		iSO.put("VN", "84");
		iSO.put("VU", "678");
		iSO.put("WF", "681");
		iSO.put("WS", "685");
		iSO.put("XC", "991");
		iSO.put("XD", "888");
		iSO.put("XG", "881");
		iSO.put("XL", "883");
		iSO.put("XP", "878");
		iSO.put("XR", "979");
		iSO.put("XS", "808");
		iSO.put("XT", "800");
		iSO.put("XV", "882");
		iSO.put("YE", "967");
		iSO.put("YT", "262");
		iSO.put("ZA", "27");
		iSO.put("ZM", "260");
		iSO.put("ZW", "263");
	}

	private void initCountryMap() {
		countryMap.add(new CountryFilterBean("1 canada", "1", "Canada"));
		countryMap.add(new CountryFilterBean(
				"1 united states of america usa us", "1",
				"United States of America"));
		countryMap.add(new CountryFilterBean("1 bahamas", "1", "Bahamas"));
		countryMap.add(new CountryFilterBean("1 barbados", "1", "Barbados"));
		countryMap.add(new CountryFilterBean("1 anguilla", "1", "Anguilla"));
		countryMap.add(new CountryFilterBean("1 antigua and barbuda", "1",
				"Antigua and Barbuda"));
		countryMap.add(new CountryFilterBean("1 british virgin islands uk",
				"1", "British Virgin Islands (UK)"));
		countryMap.add(new CountryFilterBean(
				"1 united states virgin islands us", "1",
				"United States Virgin Islands (US)"));
		countryMap.add(new CountryFilterBean("1 cayman islands uk", "1",
				"Cayman Islands (UK)"));
		countryMap.add(new CountryFilterBean("1 bermuda uk", "1",
				"Bermuda (UK)"));
		countryMap.add(new CountryFilterBean("1 grenada", "1", "Grenada"));
		countryMap.add(new CountryFilterBean("1 turks and caicos islands uk",
				"1", "Turks and Caicos Islands (UK)"));
		countryMap.add(new CountryFilterBean("1 montserrat uk", "1",
				"Montserrat (UK)"));
		countryMap.add(new CountryFilterBean("1 northern mariana islands us",
				"1", "Northern Mariana Islands (US)"));
		countryMap.add(new CountryFilterBean("1 guam us", "1", "Guam (US)"));
		countryMap.add(new CountryFilterBean("1 american samoa us", "1",
				"American Samoa (US)"));
		countryMap.add(new CountryFilterBean("1 sint marteen", "1",
				"Sint Marteen"));
		countryMap.add(new CountryFilterBean("1 saint lucia", "1",
				"Saint Lucia"));
		countryMap.add(new CountryFilterBean("1 dominica", "1", "Dominica"));
		countryMap.add(new CountryFilterBean(
				"1 saint vincent and the grenadines", "1",
				"Saint Vincent and the Grenadines"));
		countryMap.add(new CountryFilterBean("1 puerto rico us", "1",
				"Puerto Rico (US)"));
		countryMap.add(new CountryFilterBean("1 dominican republic", "1",
				"Dominican Republic"));
		countryMap.add(new CountryFilterBean("1 trinidad and tobago", "1",
				"Trinidad and Tobago"));
		countryMap.add(new CountryFilterBean("1 saint kitts and nevis", "1",
				"Saint Kitts and Nevis"));
		countryMap.add(new CountryFilterBean("1 jamaica", "1", "Jamaica"));
		countryMap
				.add(new CountryFilterBean("7 kazakhstan", "7", "Kazakhstan"));
		countryMap.add(new CountryFilterBean("7 russian federation", "7",
				"Russian Federation"));
		countryMap.add(new CountryFilterBean("7 abkhazia", "7", "Abkhazia"));
		countryMap.add(new CountryFilterBean("20 egypt", "20", "Egypt"));
		countryMap.add(new CountryFilterBean("27 south africa", "27",
				"South Africa"));
		countryMap.add(new CountryFilterBean("30 greece", "30", "Greece"));
		countryMap.add(new CountryFilterBean("31 netherlands", "31",
				"Netherlands"));
		countryMap.add(new CountryFilterBean("32 belgium", "32", "Belgium"));
		countryMap.add(new CountryFilterBean("33 france", "33", "France"));
		countryMap.add(new CountryFilterBean("34 spain", "34", "Spain"));
		countryMap.add(new CountryFilterBean("36 hungary", "36", "Hungary"));
		countryMap.add(new CountryFilterBean("39 italy", "39", "Italy"));
		countryMap.add(new CountryFilterBean("40 romania", "40", "Romania"));
		countryMap.add(new CountryFilterBean("41 switzerland", "41",
				"Switzerland"));
		countryMap.add(new CountryFilterBean("43 austria", "43", "Austria"));
		countryMap.add(new CountryFilterBean("44 united kingdom", "44",
				"United Kingdom"));
		countryMap.add(new CountryFilterBean("44 guernsey", "44", "Guernsey"));
		countryMap.add(new CountryFilterBean("44 isle of man", "44",
				"Isle of Man"));
		countryMap.add(new CountryFilterBean("44 jersey", "44", "Jersey"));
		countryMap.add(new CountryFilterBean("44 united kingdom", "44",
				"United Kingdom"));
		countryMap.add(new CountryFilterBean("45 denmark", "45", "Denmark"));
		countryMap.add(new CountryFilterBean("46 sweden", "46", "Sweden"));
		countryMap.add(new CountryFilterBean("47 norway", "47", "Norway"));
		countryMap.add(new CountryFilterBean("47 svalbard and jan mayen", "47",
				"Svalbard and Jan Mayen"));
		countryMap.add(new CountryFilterBean("48 poland", "48", "Poland"));
		countryMap.add(new CountryFilterBean("49 germany", "49", "Germany"));
		countryMap.add(new CountryFilterBean("51 perú", "51", "Perú"));
		countryMap.add(new CountryFilterBean("52 mexico", "52", "Mexico"));
		countryMap.add(new CountryFilterBean("53 cuba", "53", "Cuba"));
		countryMap.add(new CountryFilterBean("54 argentine republic", "54",
				"Argentine Republic"));
		countryMap.add(new CountryFilterBean("55 brazil", "55", "Brazil"));
		countryMap.add(new CountryFilterBean("56 chile", "56", "Chile"));
		countryMap.add(new CountryFilterBean("57 colombia", "57", "Colombia"));
		countryMap
				.add(new CountryFilterBean("58 venezuela", "58", "Venezuela"));
		countryMap.add(new CountryFilterBean("60 malaysia", "60", "Malaysia"));
		countryMap
				.add(new CountryFilterBean("61 australia", "61", "Australia"));
		countryMap.add(new CountryFilterBean("61 cocos keeling islands", "61",
				"Cocos (Keeling) Islands"));
		countryMap.add(new CountryFilterBean("61 christmas island", "61",
				"Christmas Island"));
		countryMap
				.add(new CountryFilterBean("62 indonesia", "62", "Indonesia"));
		countryMap.add(new CountryFilterBean("63 philippines", "63",
				"Philippines"));
		countryMap.add(new CountryFilterBean("64 new zealand", "64",
				"New Zealand"));
		countryMap
				.add(new CountryFilterBean("65 singapore", "65", "Singapore"));
		countryMap.add(new CountryFilterBean("66 thailand", "66", "Thailand"));
		countryMap.add(new CountryFilterBean("81 japan", "81", "Japan"));
		countryMap.add(new CountryFilterBean("82 korea south", "82",
				"Korea, South"));
		countryMap.add(new CountryFilterBean("84 viet nam", "84", "Viet Nam"));
		countryMap.add(new CountryFilterBean("86 china prc", "86", "China"));
		countryMap.add(new CountryFilterBean("90 northern cyprus", "90",
				"Northern Cyprus"));
		countryMap.add(new CountryFilterBean("90 turkey", "90", "Turkey"));
		countryMap.add(new CountryFilterBean("91 india", "91", "India"));
		countryMap.add(new CountryFilterBean("92 pakistan", "92", "Pakistan"));
		countryMap.add(new CountryFilterBean("93 afghanistan", "93",
				"Afghanistan"));
		countryMap
				.add(new CountryFilterBean("94 sri lanka", "94", "Sri Lanka"));
		countryMap.add(new CountryFilterBean("95 myanmar", "95", "Myanmar"));
		countryMap.add(new CountryFilterBean("98 iran", "98", "Iran"));
		countryMap.add(new CountryFilterBean("212 western sahara", "212",
				"Western Sahara"));
		countryMap.add(new CountryFilterBean("212 morocco", "212", "Morocco"));
		countryMap.add(new CountryFilterBean("213 algeria", "213", "Algeria"));
		countryMap.add(new CountryFilterBean("216 tunisia", "216", "Tunisia"));
		countryMap.add(new CountryFilterBean("218 libya", "218", "Libya"));
		countryMap.add(new CountryFilterBean("220 gambia", "220", "Gambia"));
		countryMap.add(new CountryFilterBean("221 senegal", "221", "Senegal"));
		countryMap.add(new CountryFilterBean("222 mauritania", "222",
				"Mauritania"));
		countryMap.add(new CountryFilterBean("223 mali", "223", "Mali"));
		countryMap.add(new CountryFilterBean("224 guinea", "224", "Guinea"));
		countryMap.add(new CountryFilterBean("225 côte d'ivoire", "225",
				"Côte d'Ivoire"));
		countryMap.add(new CountryFilterBean("226 burkina faso", "226",
				"Burkina Faso"));
		countryMap.add(new CountryFilterBean("227 niger", "227", "Niger"));
		countryMap.add(new CountryFilterBean("228 togolese republic", "228",
				"Togolese Republic"));
		countryMap.add(new CountryFilterBean("229 benin", "229", "Benin"));
		countryMap.add(new CountryFilterBean("230 mauritius", "230",
				"Mauritius"));
		countryMap.add(new CountryFilterBean("231 liberia", "231", "Liberia"));
		countryMap.add(new CountryFilterBean("232 sierra leone", "232",
				"Sierra Leone"));
		countryMap.add(new CountryFilterBean("233 ghana", "233", "Ghana"));
		countryMap.add(new CountryFilterBean("234 nigeria", "234", "Nigeria"));
		countryMap.add(new CountryFilterBean("235 chad", "235", "Chad"));
		countryMap.add(new CountryFilterBean("236 central african republic",
				"236", "Central African Republic"));
		countryMap
				.add(new CountryFilterBean("237 cameroon", "237", "Cameroon"));
		countryMap.add(new CountryFilterBean("238 cape verde", "238",
				"Cape Verde"));
		countryMap.add(new CountryFilterBean("239 são tomé and príncipe",
				"239", "São Tomé and Príncipe"));
		countryMap.add(new CountryFilterBean("240 equatorial guinea", "240",
				"Equatorial Guinea"));
		countryMap.add(new CountryFilterBean("241 gabonese republic", "241",
				"Gabonese Republic"));
		countryMap.add(new CountryFilterBean("242 republic of the congo",
				"242", "Republic of the Congo"));
		countryMap.add(new CountryFilterBean(
				"243 democratic republic of the congo", "243",
				"Democratic Republic of the Congo"));
		countryMap.add(new CountryFilterBean("244 angola", "244", "Angola"));
		countryMap.add(new CountryFilterBean("245 guinea-bissau", "245",
				"Guinea-Bissau"));
		countryMap.add(new CountryFilterBean(
				"246 british indian ocean territory", "246",
				"British Indian Ocean Territory"));
		countryMap.add(new CountryFilterBean("247 ascension island", "247",
				"Ascension Island"));
		countryMap.add(new CountryFilterBean("248 seychelles", "248",
				"Seychelles"));
		countryMap.add(new CountryFilterBean("249 sudan", "249", "Sudan"));
		countryMap.add(new CountryFilterBean("250 rwandese republic", "250",
				"Rwandese Republic"));
		countryMap
				.add(new CountryFilterBean("251 ethiopia", "251", "Ethiopia"));
		countryMap.add(new CountryFilterBean("252 somaliland", "252",
				"Somaliland"));
		countryMap.add(new CountryFilterBean("252 somalia republic", "252",
				"Somalia Republic"));
		countryMap
				.add(new CountryFilterBean("253 djibouti", "253", "Djibouti"));
		countryMap.add(new CountryFilterBean("254 kenya", "254", "Kenya"));
		countryMap
				.add(new CountryFilterBean("255 tanzania", "255", "Tanzania"));
		countryMap.add(new CountryFilterBean("256 uganda", "256", "Uganda"));
		countryMap.add(new CountryFilterBean("257 burundi", "257", "Burundi"));
		countryMap.add(new CountryFilterBean("258 mozambique", "258",
				"Mozambique"));
		countryMap.add(new CountryFilterBean("260 zambia", "260", "Zambia"));
		countryMap.add(new CountryFilterBean("261 madagascar", "261",
				"Madagascar"));
		countryMap.add(new CountryFilterBean("262 réunion france", "262",
				"Réunion (France)"));
		countryMap.add(new CountryFilterBean("262 mayotte", "262", "Mayotte"));
		countryMap
				.add(new CountryFilterBean("263 zimbabwe", "263", "Zimbabwe"));
		countryMap.add(new CountryFilterBean("264 namibia", "264", "Namibia"));
		countryMap.add(new CountryFilterBean("265 malawi", "265", "Malawi"));
		countryMap.add(new CountryFilterBean("266 lesotho", "266", "Lesotho"));
		countryMap
				.add(new CountryFilterBean("267 botswana", "267", "Botswana"));
		countryMap.add(new CountryFilterBean("268 swaziland", "268",
				"Swaziland"));
		countryMap.add(new CountryFilterBean("269 comoros", "269", "Comoros"));
		countryMap.add(new CountryFilterBean("290 saint helena", "290",
				"Saint Helena"));
		countryMap.add(new CountryFilterBean("290 tristan da cunha", "290",
				"Tristan da Cunha"));
		countryMap.add(new CountryFilterBean("291 eritrea", "291", "Eritrea"));
		countryMap.add(new CountryFilterBean("297 aruba netherlands", "297",
				"Aruba (Netherlands)"));
		countryMap.add(new CountryFilterBean("298 faroe islands denmark",
				"298", "Faroe Islands (Denmark)"));
		countryMap.add(new CountryFilterBean("299 greenland denmark", "299",
				"Greenland (Denmark)"));
		countryMap.add(new CountryFilterBean("350 gibraltar uk", "350",
				"Gibraltar (UK)"));
		countryMap
				.add(new CountryFilterBean("351 portugal", "351", "Portugal"));
		countryMap.add(new CountryFilterBean("352 luxembourg", "352",
				"Luxembourg"));
		countryMap.add(new CountryFilterBean("353 ireland", "353", "Ireland"));
		countryMap.add(new CountryFilterBean("354 iceland", "354", "Iceland"));
		countryMap.add(new CountryFilterBean("355 albania", "355", "Albania"));
		countryMap.add(new CountryFilterBean("356 malta", "356", "Malta"));
		countryMap.add(new CountryFilterBean("357 cyprus", "357", "Cyprus"));
		countryMap.add(new CountryFilterBean("358 åland islands", "358",
				"Åland Islands"));
		countryMap.add(new CountryFilterBean("358 finland", "358", "Finland"));
		countryMap
				.add(new CountryFilterBean("359 bulgaria", "359", "Bulgaria"));
		countryMap.add(new CountryFilterBean("370 lithuania", "370",
				"Lithuania"));
		countryMap.add(new CountryFilterBean("371 latvia", "371", "Latvia"));
		countryMap.add(new CountryFilterBean("372 estonia", "372", "Estonia"));
		countryMap.add(new CountryFilterBean("373 moldova", "373", "Moldova"));
		countryMap.add(new CountryFilterBean("374 armenia", "374", "Armenia"));
		countryMap.add(new CountryFilterBean("374 nagorno-karabakh", "374",
				"Nagorno-Karabakh"));
		countryMap.add(new CountryFilterBean("375 belarus", "375", "Belarus"));
		countryMap.add(new CountryFilterBean("376 andorra", "376", "Andorra"));
		countryMap.add(new CountryFilterBean("377 monaco", "377", "Monaco"));
		countryMap.add(new CountryFilterBean("378 san marino", "378",
				"San Marino"));
		countryMap.add(new CountryFilterBean("379 vatican city state", "379",
				"Vatican City State"));
		countryMap.add(new CountryFilterBean("380 ukraine", "380", "Ukraine"));
		countryMap.add(new CountryFilterBean("381 serbia republic of", "381",
				"Serbia (Republic of)"));
		countryMap.add(new CountryFilterBean("382 montenegro republic of",
				"382", "Montenegro (Republic of)"));
		countryMap.add(new CountryFilterBean("385 croatia", "385", "Croatia"));
		countryMap
				.add(new CountryFilterBean("386 slovenia", "386", "Slovenia"));
		countryMap.add(new CountryFilterBean("387 bosnia and herzegovina",
				"387", "Bosnia and Herzegovina"));
		countryMap.add(new CountryFilterBean("389 republic of macedonia",
				"389", "Republic of Macedonia"));
		countryMap.add(new CountryFilterBean("420 czech republic", "420",
				"Czech Republic"));
		countryMap
				.add(new CountryFilterBean("421 slovakia", "421", "Slovakia"));
		countryMap.add(new CountryFilterBean("423 liechtenstein", "423",
				"Liechtenstein"));
		countryMap.add(new CountryFilterBean("500 falkland islands malvinas",
				"500", "Falkland Islands (Malvinas)"));
		countryMap.add(new CountryFilterBean("501 belize", "501", "Belize"));
		countryMap.add(new CountryFilterBean("502 guatemala", "502",
				"Guatemala"));
		countryMap.add(new CountryFilterBean("503 el salvador", "503",
				"El Salvador"));
		countryMap
				.add(new CountryFilterBean("504 honduras", "504", "Honduras"));
		countryMap.add(new CountryFilterBean("505 nicaragua", "505",
				"Nicaragua"));
		countryMap.add(new CountryFilterBean("506 costa rica", "506",
				"Costa Rica"));
		countryMap.add(new CountryFilterBean("507 panama", "507", "Panama"));
		countryMap.add(new CountryFilterBean(
				"508 saint pierre and miquelon france", "508",
				"Saint Pierre and Miquelon (France)"));
		countryMap.add(new CountryFilterBean("509 haiti", "509", "Haiti"));
		countryMap.add(new CountryFilterBean("590 saint barthélemy", "590",
				"Saint Barthélemy"));
		countryMap.add(new CountryFilterBean("590 guadeloupe france", "590",
				"Guadeloupe (France)"));
		countryMap.add(new CountryFilterBean("590 saint martin", "590",
				"Saint Martin"));
		countryMap.add(new CountryFilterBean("591 bolivia", "591", "Bolivia"));
		countryMap.add(new CountryFilterBean("592 guyana", "592", "Guyana"));
		countryMap.add(new CountryFilterBean("593 ecuador", "593", "Ecuador"));
		countryMap.add(new CountryFilterBean("594 french guiana france", "594",
				"French Guiana (France)"));
		countryMap
				.add(new CountryFilterBean("595 paraguay", "595", "Paraguay"));
		countryMap.add(new CountryFilterBean("596 martinique france", "596",
				"Martinique (France)"));
		countryMap
				.add(new CountryFilterBean("597 suriname", "597", "Suriname"));
		countryMap.add(new CountryFilterBean("598 uruguay", "598", "Uruguay"));
		countryMap.add(new CountryFilterBean(
				"599 netherlands antilles netherlands", "599",
				"Netherlands Antilles (Netherlands)"));
		countryMap.add(new CountryFilterBean("599 caribbean netherlands",
				"599", "Caribbean Netherlands"));
		countryMap.add(new CountryFilterBean("599 curaçao", "599", "Curaçao"));
		countryMap.add(new CountryFilterBean("599 sint maarten", "599",
				"Sint Maarten"));
		countryMap.add(new CountryFilterBean("670 east timor", "670",
				"East Timor"));
		countryMap.add(new CountryFilterBean(
				"672 australian antarctic territory", "672",
				"Australian Antarctic Territory"));
		countryMap.add(new CountryFilterBean("672 norfolk island", "672",
				"Norfolk Island"));
		countryMap.add(new CountryFilterBean("673 brunei darussalam", "673",
				"Brunei Darussalam"));
		countryMap.add(new CountryFilterBean("674 nauru", "674", "Nauru"));
		countryMap.add(new CountryFilterBean("675 papua new guinea", "675",
				"Papua New Guinea"));
		countryMap.add(new CountryFilterBean("676 tonga", "676", "Tonga"));
		countryMap.add(new CountryFilterBean("677 solomon islands", "677",
				"Solomon Islands"));
		countryMap.add(new CountryFilterBean("678 vanuatu", "678", "Vanuatu"));
		countryMap.add(new CountryFilterBean("679 fiji", "679", "Fiji"));
		countryMap.add(new CountryFilterBean("680 palau", "680", "Palau"));
		countryMap.add(new CountryFilterBean("681 wallis and futuna france",
				"681", "Wallis and Futuna (France)"));
		countryMap.add(new CountryFilterBean("682 cook islands nz", "682",
				"Cook Islands (NZ)"));
		countryMap.add(new CountryFilterBean("683 niue", "683", "Niue"));
		countryMap.add(new CountryFilterBean("685 samoa", "685", "Samoa"));
		countryMap
				.add(new CountryFilterBean("686 kiribati", "686", "Kiribati"));
		countryMap.add(new CountryFilterBean("687 new caledonia france", "687",
				"New Caledonia (France)"));
		countryMap.add(new CountryFilterBean("688 tuvalu", "688", "Tuvalu"));
		countryMap.add(new CountryFilterBean("689 french polynesia france",
				"689", "French Polynesia (France)"));
		countryMap.add(new CountryFilterBean("690 tokelau", "690", "Tokelau"));
		countryMap.add(new CountryFilterBean(
				"691 federated states of micronesia", "691",
				"Federated States of Micronesia"));
		countryMap.add(new CountryFilterBean("692 marshall islands", "692",
				"Marshall Islands"));
		countryMap.add(new CountryFilterBean("800 toll-free telephone number",
				"800", "Toll-free telephone number"));
		countryMap.add(new CountryFilterBean("808 shared cost service", "808",
				"Shared Cost Service"));
		countryMap.add(new CountryFilterBean("850 korea north", "850",
				"Korea, North"));
		countryMap.add(new CountryFilterBean("852 hong kong prc", "852",
				"Hong Kong (PRC)"));
		countryMap.add(new CountryFilterBean("853 macau prc", "853",
				"Macau (PRC)"));
		countryMap
				.add(new CountryFilterBean("855 cambodia", "855", "Cambodia"));
		countryMap.add(new CountryFilterBean("856 laos", "856", "Laos"));
		countryMap.add(new CountryFilterBean("857 anac satellite service",
				"857", "ANAC satellite service"));
		countryMap.add(new CountryFilterBean("858 anac satellite service",
				"858", "ANAC satellite service"));
		countryMap.add(new CountryFilterBean("870 inmarsat snac service",
				"870", "Inmarsat SNAC service"));
		countryMap.add(new CountryFilterBean(
				"878 universal personal telecommunications services upts",
				"878", "Universal Personal Telecommunications services"));
		countryMap.add(new CountryFilterBean("880 bangladesh", "880",
				"Bangladesh"));
		countryMap.add(new CountryFilterBean(
				"881 global mobile satellite system gmss", "881",
				"Global Mobile Satellite System"));
		countryMap.add(new CountryFilterBean("882 international networks",
				"882", "International Networks"));
		countryMap.add(new CountryFilterBean("883 international networks",
				"883", "International Networks"));
		countryMap.add(new CountryFilterBean("886 taiwan", "886", "Taiwan"));
		countryMap.add(new CountryFilterBean(
				"888 telecommunications for disaster relief by ocha", "888",
				"Telecommunications for Disaster Relief by OCHA"));
		countryMap
				.add(new CountryFilterBean("960 maldives", "960", "Maldives"));
		countryMap.add(new CountryFilterBean("961 lebanon", "961", "Lebanon"));
		countryMap.add(new CountryFilterBean("962 jordan", "962", "Jordan"));
		countryMap.add(new CountryFilterBean("963 syria", "963", "Syria"));
		countryMap.add(new CountryFilterBean("964 iraq", "964", "Iraq"));
		countryMap.add(new CountryFilterBean("965 kuwait", "965", "Kuwait"));
		countryMap.add(new CountryFilterBean("966 saudi arabia", "966",
				"Saudi Arabia"));
		countryMap.add(new CountryFilterBean("967 yemen", "967", "Yemen"));
		countryMap.add(new CountryFilterBean("968 oman", "968", "Oman"));
		countryMap.add(new CountryFilterBean("970 palestine", "970",
				"Palestine"));
		countryMap.add(new CountryFilterBean("971 united arab emirates", "971",
				"United Arab Emirates"));
		countryMap.add(new CountryFilterBean("972 israel", "972", "Israel"));
		countryMap.add(new CountryFilterBean("973 bahrain", "973", "Bahrain"));
		countryMap.add(new CountryFilterBean("974 qatar", "974", "Qatar"));
		countryMap.add(new CountryFilterBean("975 bhutan", "975", "Bhutan"));
		countryMap
				.add(new CountryFilterBean("976 mongolia", "976", "Mongolia"));
		countryMap.add(new CountryFilterBean("977 nepal", "977", "Nepal"));
		countryMap.add(new CountryFilterBean(
				"979 international premium rate serviceiprs", "979",
				"International Premium Rate Service"));
		countryMap
				.add(new CountryFilterBean(
						"991 international telecommunications public correspondence service trial itpcs",
						"991",
						"International Telecommunications Public Correspondence Service trial (ITPCS)"));
		countryMap.add(new CountryFilterBean("992 tajikistan", "992",
				"Tajikistan"));
		countryMap.add(new CountryFilterBean("993 turkmenistan", "993",
				"Turkmenistan"));
		countryMap.add(new CountryFilterBean("994 azerbaijani republic", "994",
				"Azerbaijani Republic"));
		countryMap.add(new CountryFilterBean("995 georgia", "995", "Georgia"));
		countryMap.add(new CountryFilterBean("996 kyrgyz republic", "996",
				"Kyrgyz Republic"));
		countryMap.add(new CountryFilterBean("998 uzbekistan", "998",
				"Uzbekistan"));

	}

	public class CodeBean {
		private String phone; // null or 0<length
		private String zcode; // null or 0<length
		private boolean correct; // true can be used; false need to be
									// re-confirmed

		public CodeBean() {
			setPhone(null);
			setZcode(null);
			setCorrect(false);
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof CodeBean)) {
				return false;
			}
			CodeBean ob = (CodeBean) o;
			if (((phone != null && phone.equalsIgnoreCase(ob.getPhone())) || (phone == null && ob
					.getPhone() == null))
					&& ((zcode != null && zcode.equalsIgnoreCase(ob.getZcode())) || (zcode == null && ob
							.getZcode() == null)))
				return true;
			return false;
		};

		public boolean isCompleted() {
			if (phone == null || zcode == null) {
				return false;
			}
			return true;
		}

		public void setZcode(String zcode) {
			if (zcode == null || zcode.length() < 1)
				zcode = null;
			this.zcode = zcode;
		}

		public String getZcode() {
			return zcode;
		}

		public void setPhone(String phone) {
			if (phone == null || phone.length() < 1)
				phone = null;
			this.phone = phone;
		}

		public String getPhone() {
			return phone;
		}

		public void setCorrect(boolean correct) {
			this.correct = correct;
		}

		public boolean isCorrect() {
			return correct;
		}
	}

	private class CountryFilterBean extends CountryBean {
		private String key; // null or 0<length

		public CountryFilterBean(String key, String zcode, String country) {
			super(zcode, country);
			setKey(key);
		}

		public void setKey(String key) {
			if (key == null || key.length() < 1)
				key = null;
			this.key = key;
		}

		public String getKey() {
			return key;
		}

	}

	public class CountryBean {
		private String zcode; // null or 0<length
		private String country; // null or 0<length

		public CountryBean(String zcode, String country) {
			setZcode(zcode);
			setCountry(country);
		}

		public void setZcode(String zcode) {
			if (zcode == null || zcode.length() < 1)
				zcode = null;
			this.zcode = zcode;
		}

		public String getZcode() {
			return zcode;
		}

		public void setCountry(String country) {
			if (country == null || country.length() < 1)
				country = null;
			this.country = country;
		}

		public String getCountry() {
			return country;
		}
	}
}
