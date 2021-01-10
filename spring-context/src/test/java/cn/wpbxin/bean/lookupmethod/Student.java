package cn.wpbxin.bean.lookupmethod;

public class Student extends User{

	@Override
	public void lookupMethod() {
		System.out.println("This is the Student's lookupMethod!");
	}
}
