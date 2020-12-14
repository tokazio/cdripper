package fr.tokazio.ripper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CddbTest {

    @Test
    public void year() {
        //given
        Cddb cddb = new Cddb();
        //when
        cddb.getExtendedData("");
        //then
        assertThat(cddb.cddbData().getYear()).isEqualTo("2002");
    }
}
