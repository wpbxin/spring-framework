package cn.wpbxin.bean.lookupmethod;

public class Teacher extends User{

	@Override
	public void lookupMethod() {
		System.out.println("This is the Teacher's lookupMethod!");
	}
}
