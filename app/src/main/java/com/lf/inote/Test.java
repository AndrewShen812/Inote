/**
 * Project name：Inote
 * Create time：2017/1/11 11:23
 * Copyright: 2017 GALAXYWIND Network Systems Co.,Ltd.All rights reserved.
 */
package com.lf.inote;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by sy on 2017/1/11.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/1/11 11:23<br>
 * Revise Record:<br>
 * 2017/1/11: 创建并完成初始实现<br>
 */
public class Test {

    static class Person implements Serializable {
        String name;
        String tel;
        int age;

        public Person(String name, String tel, int age) {
            this.name = name;
            this.tel = tel;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", tel='" + tel + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    public static void main(String[] args)
    {
        Integer a = 128, b = 128;
        System.out.println(a == b);
        Integer c = 127, d = 127;
        System.out.println(c == d);

//        serialize();
        deserialize();
    }

    private static void serialize() {
        try {
            Person p = new Person("shenyong", "13548111470", 24);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("E:/person.txt"));
            oos.writeObject(p);
            oos.close();
            System.out.println("done.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deserialize() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("E:/person.txt"));
            Person p = (Person) ois.readObject();
            ois.close();
            System.out.println(p.toString());
            System.out.println("done.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
