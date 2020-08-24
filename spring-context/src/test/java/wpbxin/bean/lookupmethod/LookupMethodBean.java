package wpbxin.bean.lookupmethod;

public abstract class LookupMethodBean {

	public void showResult() {
		this.getBean().lookupMethod();
	}
	public abstract User getBean();
}
