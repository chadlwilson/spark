package spark;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.Test;

public class ExceptionMapperTest {

    private final ExceptionHandlerImpl<Exception> testHandler = newHandler();

    private static ExceptionHandlerImpl<Exception> newHandler() {
        return new ExceptionHandlerImpl<>(Exception.class) {
            @Override
            public void handle(Exception exception, Request request, Response response) {
            }
        };
    }

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

    @Test
    public void testGetNullIfNoHandler() {
        ExceptionMapper mapper = new ExceptionMapper();
        assertNull(mapper.getHandler(new Exception()));
        assertEquals("expect result to be cached", 1, mapper.size());
    }

    @Test
    public void testGetDirectlyMappedExceptionHandler() {
        ExceptionMapper mapper = new ExceptionMapper();
        mapper.map(Exception.class, testHandler);

        assertSame(testHandler, mapper.getHandler(new Exception()));
        assertEquals(1, mapper.size());
    }

    @Test
    public void testGetSuperclassMappedExceptionHandler() {
        ExceptionMapper mapper = new ExceptionMapper();
        mapper.map(Exception.class, testHandler);
        assertEquals(1, mapper.size());

        assertSame(testHandler, mapper.getHandler(new RuntimeException()));
        assertEquals("expect result to be cached", 2, mapper.size());
    }

    @Test
    public void testGetSuperclassMappedExceptionHandlerWithThreeLevels() {
        ExceptionMapper mapper = new ExceptionMapper();
        mapper.map(Exception.class, testHandler);
        assertEquals(1, mapper.size());

        assertSame(testHandler, mapper.getHandler(new CustomRuntimeException()));
        assertEquals("expect result to be cached only at requested level", 2, mapper.size());
    }

    @Test
    public void testGetSuperclassMappedExceptionHandlerFindsFirstAppropriateHandler() {
        ExceptionMapper mapper = new ExceptionMapper();
        ExceptionHandlerImpl<Exception> anotherHandler = newHandler();
        mapper.map(Exception.class, anotherHandler);
        mapper.map(RuntimeException.class, testHandler);

        assertSame(testHandler, mapper.getHandler(new CustomRuntimeException()));
        assertSame(anotherHandler, mapper.getHandler(new Exception()));
        assertSame(anotherHandler, mapper.getHandler(new IOException()));
    }

    private static class CustomRuntimeException extends RuntimeException {}
}
