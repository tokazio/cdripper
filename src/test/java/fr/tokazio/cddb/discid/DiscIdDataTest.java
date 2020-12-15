package fr.tokazio.cddb.discid;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DiscIdDataTest {

    @Test
    public void parse() {
        //given
        final String out = "cc10ae0f 15 182 13005 28870 46377 62425 87930 112267 128607 143960 159022 190262 233922 248090 275132 303485 4272";

        //when
        final DiscIdData discIdData = new DiscIdData(out);

        //then
        assertThat(discIdData.getDiscId()).isEqualTo("cc10ae0f");
        assertThat(discIdData.getNbTracks()).isEqualTo(15);
        assertThat(discIdData.getTotalLengthInSec()).isEqualTo(4272);
        assertThat(discIdData.getFrameOffsets()).hasSize(15);
    }

}
