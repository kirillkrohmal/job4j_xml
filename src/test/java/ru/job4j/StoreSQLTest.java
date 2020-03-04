package ru.job4j;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StoreSQLTest {
    @Test
    public void generateN() throws Exception {
        Config config = new Config();
        try (StoreSQL storeSQL = new StoreSQL(config)) {
            config.init();
            int size = 138341;
            storeSQL.generate(size);
            assertThat(storeSQL.findAll(), is(size));
        }
    }
}
