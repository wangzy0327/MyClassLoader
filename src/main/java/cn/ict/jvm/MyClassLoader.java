package cn.ict.jvm;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class MyClassLoader extends ClassLoader{

    private static Logger logger = LoggerFactory.getLogger(MyClassLoader.class);

    @Override
    protected Class<?> findClass(String name){
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        byte[] bytes = null;
        try {
            //注意: 此处有坑 String.replaceAll() 是正则表达式匹配 . 需要转义为 \\.
            File f = new File("C:/test/",name.replaceAll("\\.","/").concat(".class"));
            fis = new FileInputStream(f);
            baos = new ByteArrayOutputStream();
            int b = 0;

            while((b = fis.read()) != -1){
                baos.write(b);
            }
            bytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return defineClass(name,bytes,0,bytes.length);
//        return super.findClass(name);
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        ClassLoader l = new MyClassLoader();
        String name = "cn.ict.jvm.impl.Hello";
        String res = name.replaceAll("\\.","/").concat(".class");
        logger.debug(res);
        Class clazz = l.loadClass("cn.ict.jvm.impl.Hello");
        IHello hello =
                (IHello)clazz.getDeclaredConstructor().newInstance();
//        o.m();
        hello.m();

        logger.info(clazz.getClassLoader().toString());
        logger.info(hello.getClass().getClassLoader().toString());
        logger.info(l.getClass().getClassLoader().toString());
        logger.info(l.getParent().toString());
        logger.info(l.getParent().getParent().toString());
        logger.info((l.getParent().getParent().getParent()==null)?"null":l.getParent().getParent().getParent().toString());

//        System.out.println(clazz.getClassLoader());
//        System.out.println(hello.getClass().getClassLoader());
//        System.out.println(l.getClass().getClassLoader());
//        System.out.println(l.getParent());
    }
}
