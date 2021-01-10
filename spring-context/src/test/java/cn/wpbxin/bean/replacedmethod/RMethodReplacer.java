package cn.wpbxin.bean.replacedmethod;

import org.springframework.beans.factory.support.MethodReplacer;

import java.lang.reflect.Method;

public class RMethodReplacer implements MethodReplacer {
	@Override
	public Object reimplement(Object obj, Method method, Object[] args) throws Throwable {
		System.out.println("Now this is the replacer method!");
		return null;
	}
}
