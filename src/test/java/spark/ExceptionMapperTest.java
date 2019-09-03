package spark;

import static org.junit.Assert.assertSame;

import java.lang.reflect.Field;

import org.junit.Test;

public class ExceptionMapperTest {

	@Test
	public void testGetInstance_whenDefaultInstanceIsNull() throws Exception {
		Field instanceField = ExceptionMapper.class.getDeclaredField("servletInstance");
		instanceField.setAccessible(true);
		instanceField.set(null, null);
		
		ExceptionMapper exceptionMapper = ExceptionMapper.getServletInstance();
		assertSame("Should be same because ExceptionMapper is a singleton", instanceField.get(null), exceptionMapper);
	}

	@Test
	public void testGetInstance_whenDefaultInstanceIsNotNull() throws Exception {
		Field instanceField = ExceptionMapper.class.getDeclaredField("servletInstance");
		instanceField.setAccessible(true);

		ExceptionMapper.getServletInstance(); // initialize singleton
		
		ExceptionMapper exceptionMapper = ExceptionMapper.getServletInstance();
		assertSame("Should be same because ExceptionMapper is a singleton", instanceField.get(null), exceptionMapper);
	}
}
