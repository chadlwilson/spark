package spark;



import org.junit.Test;
import spark.utils.ReflectionTestUtils;

import static org.junit.Assert.assertSame;

public class ExceptionMapperTest {


    @Test
    public void testGetInstance_whenDefaultInstanceIsNull() {
        //given
        ExceptionMapper exceptionMapper = null;
        ReflectionTestUtils.setField(ExceptionMapper.class, "servletInstance", exceptionMapper);

        exceptionMapper = ExceptionMapper.getServletInstance();
        assertSame("Should be same because ExceptionMapper is a singleton", ReflectionTestUtils.getField(ExceptionMapper.class, "servletInstance"), exceptionMapper);
    }

    @Test
    public void testGetInstance_whenDefaultInstanceIsNotNull() {
        //given
        ExceptionMapper.getServletInstance(); //initialize Singleton

        //then
        ExceptionMapper exceptionMapper = ExceptionMapper.getServletInstance();
        assertSame("Should be same because ExceptionMapper is a singleton", ReflectionTestUtils.getField(ExceptionMapper.class, "servletInstance"), exceptionMapper);
    }
}
