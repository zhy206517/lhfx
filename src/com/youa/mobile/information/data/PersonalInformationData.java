package com.youa.mobile.information.data;

import com.youa.mobile.R;
import com.youa.mobile.information.param.InformationConstant;
import com.youa.mobile.parser.JsonObject;

public class PersonalInformationData {
	public static final String RELATION_STATUS_NOTHINE = "0";
	public static final String RELATION_STATUS_FOLLOW = "1";
	public static final String RELATION_STATUS_FANS_FOLLOW = "2";
	public static final String SEX_MAN = "1";
	public static final String SEX_WOMAN = "2";
	private String userId;
	private String userName;
	private String sexInt;
	private String province;
	private String city;
	private String district;
	private String birthdayYear;
	private String birthdayMonth;
	private String birthdayDay;
	private String introduce;
	private String headerImageId;
	private String relationStatus = "-1";
	private String userType;
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	//I/System.out(  973): jsonString:{"err":"mcphp.ok","data":{"youasession":"06208f0eba1e421176740a58470da8ddf5876c","userid":"21147e1b4fb511dc0
	//4239ea0","username":"lhusr38","youaidentity":"5a2d7c464c9e6e8115de6876fa3c0a48","head_img":"6181025259978892180","rpcret":{"userid":"2a357d3
	//8387d5e1e02f974fb","loginpass":"","email":"","mobilephone":"","name_sign":"4697430156962131076","username":"liuhua","thirdid":"2a357d38387d5
	//e1e02f974fb","third_type":"1","login_type":"4","userstate":"16","regtime":"200000000","lasttime":"1282656274","regip":0,"lastip":"2887111541
	//","figure":"","realname":"","sex":"","birth_year":"1900","birth_month":"1","birth_day":"1","province":"","city":"","head_imid":"","district"
	//:"","head_img_source":""}}}
	public PersonalInformationData(JsonObject json) { 
		JsonObject subjson = json.getJsonObject("rpcret");
		if (subjson == null || subjson.size() < 1) {
			return;
		}
		userId = subjson.getString(InformationConstant.result_key_uid);
		userName = subjson.getString(InformationConstant.result_key_username);
		headerImageId = subjson.getString(InformationConstant.result_key_head_imid);
		sexInt = subjson.getString(InformationConstant.result_key_sex);
		province = subjson.getString(InformationConstant.result_key_province);
		city = subjson.getString(InformationConstant.result_key_city);
		district = subjson.getString(InformationConstant.result_key_district);
		birthdayYear = subjson.getString(InformationConstant.result_key_birth_year);
		userType=subjson.getString(InformationConstant.result_key_type);
		birthdayMonth = "";
		birthdayDay = "";
		if(birthdayYear != null
				&& !"".equals(birthdayYear)) {			
			birthdayMonth = subjson.getString(InformationConstant.result_key_birth_month);
			birthdayDay = subjson.getString(InformationConstant.result_key_birth_day);
			if(birthdayMonth == null || "".equals(birthdayMonth)) {
				birthdayMonth = "1";
			}
			
			if(birthdayDay == null || "".equals(birthdayDay)) {
				birthdayDay = "1";
			}
		}		
		introduce = subjson.getString(InformationConstant.result_key_signature);
	}
	
	public PersonalInformationData(
			String userId,
			String userName,
			String sex,
			String city,
			String birthdayYear,
			String birthdayMonth,
			String birthdayDay,
			String introduce
	) { 
		this.userId = userId;
		this.userName = userName;
		this.sexInt = sex;
		this.city = city;
		this.userId = userId;
		this.birthdayYear = birthdayYear;
		this.birthdayMonth = birthdayMonth;
		this.birthdayDay = birthdayDay;
		this.introduce = introduce;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getUserName() {
		return userName;
	}

	public int getSexResId() {
		if(sexInt == null) {
			return R.string.common_null_space;
		} else if(sexInt.equals(SEX_MAN)) {
			return R.string.information_man;
		} else {
			return R.string.information_woman;
		}
	}
	
	public String getSexInt() {
		return sexInt;
	}

	public String getCity() {
		return city;
	}
	public String getIntroduce() {
		return introduce;
	}

	public String getBirthdayYear() {
		return birthdayYear;
	}

	public String getBirthdayMonth() {
		return birthdayMonth;
	}

	public String getBirthdayDay() {
		return birthdayDay;
	}
	public String getBirthday() {
		if(birthdayYear == null || "".equals(birthdayYear)) {
			return "";
		} else {
			return birthdayYear + "-" + birthdayMonth + "-" + birthdayDay;
		}
	}

	public String getHeaderImageId() {
		return headerImageId;
	}

	public String getProvince() {
		return province;
	}

	public String getDistrict() {
		return district;
	}

//	public boolean isFollowed() {
//		if(RELATION_STATUS_FOLLOW.equals(relationStatus)
//				&& RELATION_STATUS_FANS_FOLLOW.equals(relationStatus)) {
//			return true;
//		} else {
//			return false;
//		}	
//	}

	public void setRelationStatus(String relationStatus) {
		this.relationStatus = relationStatus;
	}
	
	public String getRelationStatus() {
		return relationStatus;
	}
}
