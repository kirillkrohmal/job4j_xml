В этом задании нужно решить тестовое задание в фирму МАГНИТ.

В задании нужно произвести процесс манипуляции над данными.

В качестве базы данных здесь трeбуют использовать SQLLite.

Давай подключим эту базу к нашему проекту.

База SQLLite является встроенной базой и не требует установки. Единственно. что нужно сделать это добавить зависимости.

<!-- https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.21.0.1</version>
</dependency>

Общий процесс.

Весь процесс выполнения программы можно описать в виде конвейера.

1. Генерация данных в SQLLite. Описывается классом StoreSQL

   StoreSQL(Config) - Config - объект содержащий настройки для подключения к базе.

Ниже представлен класс Config.

package ru.job4j.array;

import java.io.InputStream;
import java.util.Properties;

public class Config {
    private final Properties values = new Properties();

    public void init() {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("app.properties")) {
            values.load(in);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public String get(String key) {
        return this.values.getProperty(key);
    }
}

Файл app.properties должен находиться в папке resources.



Внутри файла должны быть пары ключ значение.

url=...
username=...
password=...

По ссылке можно найти, как создать новую базу данных через JDBC.

http://www.sqlitetutorial.net/sqlite-java/create-database/

Если база уже существует, нужно проверить, что она есть. Если ее нет нужно создать заново.

Каркас класса StoreSQL.

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

public class StoreSQL implements AutoCloseable {
    private final Config config;
    private Connection connect;

    public StoreSQL(Config config) {
        this.config = config;
    }

    public void generate(int size) {

    }

    public List<Entry> load() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void close() throws Exception {
        if (connect != null) {
            connect.close();
        }
    }
}

   метод generate(int size) - генерирует в базе данных n записей.

   описывается схемой

   create table entry {
         field integer;
   }



2. После запуска приложение:
2.1) Если таблица entry в БД отсутствует, то создает ее.
2.2) вставляет в таблицу entry n записей со значениями 1..N. Если в таблице account
находились записи, то они удаляются перед вставкой.

3. Генерация XML из данных базы. Описывается класс StoreXML

  StoreXML(File target) - target - Файл куда будет сохраняться данные.

   Метод save(List<Entry> list) - сохраняет данные из list в файл target.

Данные нужно сохранить в виде XML.

<entries>
<entry>
<field>значение поля field</field>
</entry>
...
<entry>
<field>значение поля field</field>
</entry>
</entries>

Для создания xml файла нужно использовать технологию JAXB.

Элементарный пример.

Создаем модель описания данных JavaBean.

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

public class XmlUsage {

    @XmlRootElement
    public static class User {
        private List<Field> values;

        public User() {
        }

        public User(List<Field> values) {
            this.values = values;
        }

        public List<Field> getValues() {
            return values;
        }

        public void setValues(List<Field> values) {
            this.values = values;
        }
    }

    @XmlRootElement
    public static class Field {
        private int value;

        public Field() {
        }

        public Field(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }


    public static void main(String[] args) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(User.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(
                new User(Arrays.asList(new Field(1), new Field(2))),
                System.out
        );
    }
}

Вывод в консоли.

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<user>
 <values>
 <value>1</value>
 </values>
 <values>
 <value>2</value>
 </values>
</user>

Класс User, Field = описываются JavaBean.


4. Преобразовать полученный файл из пункта 3 в файл другого XML формата через XSTL.

Этот процесс будет описываться классом - ConvertXSQT

  Метод convert(File source, File dest, File scheme) - читает файл source и преобразовывает его в файл dest за счет XSTL схемы scheme.

Получаемый после преобразования  файл должен выглядеть следующим образом.

<entries>
<entry field="значение поля field">
...
<entry field="значение поля field">
</entries>

Элементарный пример.

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;

public class Stylizer {
    // ...
    public static void main (String args[]) throws TransformerException {
        String xsl = "<?xml version=\"1.0\"?>\n" +
                "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">\n" +
                "<xsl:template match=\"/\">\n" +
                "<entries>" +
                "   <xsl:for-each select=\"user/values\">\n" +
                "       <entry>" +
                "           <xsl:attribute name=\"href\">" +
                "               <xsl:value-of select=\"value\"/>" +
                "           </xsl:attribute>" +
                "       </entry>\n" +
                "   </xsl:for-each>\n" +
                " </entries>\n" +
                "</xsl:template>\n" +
                "</xsl:stylesheet>\n";

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<user>\n" +
                " <values>\n" +
                " <value>1</value>\n" +
                " </values>\n" +
                " <values>\n" +
                " <value>2</value>\n" +
                " </values>\n" +
                "</user>";
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(
                new StreamSource(
                        new ByteArrayInputStream(xsl.getBytes()))
        );
        transformer.transform(new StreamSource(
                        new ByteArrayInputStream(xml.getBytes())),
                new StreamResult(System.out)
        );
    }
}

Вывод в консоль.

<?xml version="1.0" encoding="UTF-8"?>
<entries>
   <entry href="1"/>
   <entry href="2"/>
</entries>

5. Приложение парсит выходной файл из пункта 4 и выводит арифметическую сумму значений всех атрибутов
field в консоль.

Для парсинга использовать SAX.  https://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html

6. При больших N (~1000000) время работы приложения не должно быть более пяти минут.

Рекомендации.

1. Почитайте про режим autocommit = false.
2. При использовании режима autocommit  = false - нужно в блоке catch откатывать транзакцию transaction.rollback();
3. Для парсинга используйте JAXB - https://www.mkyong.com/java/jaxb-hello-world-example/
4. Для преобразования xslt - https://docs.oracle.com/javase/tutorial/jaxp/xslt/transformingXML.html