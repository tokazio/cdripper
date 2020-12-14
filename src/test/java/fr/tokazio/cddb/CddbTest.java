package fr.tokazio.cddb;

import fr.tokazio.cddb.discid.DiscIdData;

public class CddbTest {

    //    @Test
    public void test() throws CDDBException {
        //given
        final DiscIdData discIdData = new DiscIdData("cc10ae0f 15 182 13005 28870 46377 62425 87930 112267 128607 143960 159022 190262 233922 248090 275132 303485 4272");
        //when
        final CddbData cddbData = new fr.tokazio.ripper.CddbTest().getCddb(discIdData);
        //then
        System.out.println(cddbData);
    }
}
