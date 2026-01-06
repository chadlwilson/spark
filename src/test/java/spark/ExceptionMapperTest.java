package spark;

import java.io.IOException;

import org.junit.Test;
import spark.utils.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

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
    public void testGetInstance_whenDefaultInstanceIsNull() {
        ExceptionMapper exceptionMapper = null;
        ReflectionTestUtils.setField(ExceptionMapper.class, "servletInstance", exceptionMapper);

        exceptionMapper = ExceptionMapper.getServletInstance();
        assertSame("Should be same because ExceptionMapper is a singleton", ReflectionTestUtils.getField(ExceptionMapper.class, "servletInstance"), exceptionMapper);
    }

    @Test
    public void testGetInstance_whenDefaultInstanceIsNotNull() {
        ExceptionMapper.getServletInstance(); //initialize Singleton

        ExceptionMapper exceptionMapper = ExceptionMapper.getServletInstance();
        assertSame("Should be same because ExceptionMapper is a singleton", ReflectionTestUtils.getField(ExceptionMapper.class, "servletInstance"), exceptionMapper);
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
