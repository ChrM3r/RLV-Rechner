package bin;

import javafx.embed.swing.JFXPanel;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ControllerTest {

    Controller controller;
    LocalDate datumJanuar;

    @BeforeAll
    public void setUp() throws Exception {

        //Initialisierung JavaFX
        new JFXPanel();

        //Initialisierung des Controllers
        controller = new Controller();

        //Globale Testdaten
        Calendar cal = new GregorianCalendar();
        datumJanuar = new LocalDate(cal.get(Calendar.YEAR), 1, 1);

    }


    @Test
    void restTageSonstige() throws IllegalFieldValueException {

        try {
            //Testdaten anlegen
            Calendar cal = new GregorianCalendar();
            LocalDate datumFebruar = new LocalDate(cal.get(Calendar.YEAR), 2, 1);
            LocalDate datumMaerz = new LocalDate(cal.get(Calendar.YEAR), 3, 1);
            LocalDate datumJahresende = new LocalDate(cal.get(Calendar.YEAR), 12, 31);


            //Test Abstand berechnen - ohne Exception
            assertEquals(31, Controller.restTageSonstige(datumJanuar, datumFebruar));
            assertEquals(305, Controller.restTageSonstige(datumMaerz, datumJahresende));

            //Test Abstand berechnen - mit Exception
            LocalDate datumFalsch = new LocalDate(cal.get(Calendar.YEAR), 25, 25);
            Controller.restTageSonstige(datumFalsch, datumJahresende);

        } catch (IllegalFieldValueException e) {
            return;
        }
        fail("Irgendetwas ist schief gelaufen");
    }

    @Test
    void erstattungBerechnen() throws ArithmeticException{

        assertEquals("91317,37", Controller.erstattungBerechnen(100000, 29, 334));
        assertEquals("10628,74", Controller.erstattungBerechnen(50000, 263, 334));

        try {
            Controller.erstattungBerechnen(10000, 0, 0);
        } catch (ArithmeticException e){
            return;
        }
    }

    @Test

    void berechnenButton(){

        Controller controllerMock = mock(Controller.class);
        when(controllerMock.datumAusEingabe("gemocktes Datum")).thenReturn(datumJanuar);
        assertEquals(datumJanuar, controllerMock.datumAusEingabe("gemocktes Datum"));

        //Grundsätzlich habe ich das Konzept des Mockens verstanden,
        //nur bietet sich meine Test-Applikation nicht wirklich zum mocken an...

    }
}