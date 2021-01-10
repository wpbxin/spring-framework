package cn.wpbxin.bean.customxml;

/**
 * 自定义bean
 */
public class CustomComponent {

	private String name;
	private boolean flag;

	@Override
	public String toString() {
		return "CustomComponent{" +
				"name='" + name + '\'' +
				", flag=" + flag +
				'}';
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
}
