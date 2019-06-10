package bean;

import java.util.List;
import java.util.Map;

public class Result {
	public String errorCode;
	public String type;
	public List<List<Map<String,String>>> translateResult;
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<List<Map<String, String>>> getTranslateResult() {
		return translateResult;
	}
	public void setTranslateResult(List<List<Map<String, String>>> translateResult) {
		this.translateResult = translateResult;
	}
	@Override
	public String toString() {
		return "Result [errorCode=" + errorCode + ", type=" + type + ", translateResult=" + translateResult + "]";
	}
	
}
