package configs;

import lombok.Getter;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.*;

public class Config extends Properties {
    @Getter
    private final String address;
    public Config(String address) {
        super();
        this.address=address;
        try {
            Reader fileReader = new FileReader(address);
            this.load(fileReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <E> E getProperty(Class<E> c, String propertyName) {
        return getObject(c,getProperty(propertyName));
    }

    public <E> List<E> getPropertyList(Class<E> c,String propertyName){
        List<E> list=new ArrayList<>();
        String[] values=getProperty(propertyName).split(",");
        for (String value : values) {
            list.add(getObject(c, value));
        }
        return list;
    }

    private <E> E getObject(Class<E> c, String value) {
        E e = null;
        try {
            Constructor<E> constructor = c.getConstructor(String.class);
            e = constructor.newInstance(value);
        } catch (ReflectiveOperationException reflectiveOperationException) {
            reflectiveOperationException.printStackTrace();
        }
        return e;
    }
}