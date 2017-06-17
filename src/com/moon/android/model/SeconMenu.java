package com.moon.android.model;

import java.util.List;

import com.moon.android.model.AllListModel.Submenu.Content;

/**
 * @author Administrator
 * 二级菜单
 */
public class SeconMenu {

	/**显示名称*/
	public String classify="全部";
	
	/**请求参数--按地区&年份&语言等分类*/
	public String type=null;
	
	/**请求参数--分类对应的请求值*/
	private String val=null;
	public List<VodProgram> content=null;
	
	public List<VodProgram> getContent() {
		return content;
	}

	public void setContent(List<VodProgram> content) {
		this.content = content;
	}

	public String getClassify() {
		return classify;
	}

	public void setClassify(String classify) {
		this.classify = classify;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}
}
