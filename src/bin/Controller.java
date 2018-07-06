package bin;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;
import org.joda.time.Days;
import org.joda.time.LocalDate;
//import org.joda.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;

public class Controller {

    private static String ergbnisClipboard;
    @FXML
    private DatePicker postEingangsDatum = new DatePicker();
    @FXML
    private DatePicker auszahlungsDatum = new DatePicker();

    private final DateTimeFormatter fastFormatter = DateTimeFormatter.ofPattern("ddMMuuuu");
    private final DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");



    @FXML
    private NumberTextField versPraemie = new NumberTextField();
    @FXML
    private TextField ergebnis = new TextField();
    @FXML
    private Label versDauerErgebnis = new Label();
    @FXML
    private Label versRestErgebnis = new Label();

    private static int restTageSonstige(LocalDate datum1, LocalDate datum2) {

        return Days.daysBetween(datum1, datum2).getDays();
    }

    private static LocalDate datumAusEingabe(String eingabeDatum) {

        LocalDate datum;

        String[] datumGetrennt = eingabeDatum.split("[.]");

        if (datumGetrennt.length != 3) {
            datum = new LocalDate(9999, 12, 31);
        } else {
            int tag = Integer.parseInt(datumGetrennt[0]);
            int monat = Integer.parseInt(datumGetrennt[1]);
            int jahr = Integer.parseInt(datumGetrennt[2]);
            datum = new LocalDate(jahr, monat, tag);
        }
        return datum;
    }

    private static int restTageJE(LocalDate datum) {

        Calendar cal = new GregorianCalendar();
        LocalDate jahresEnde = new LocalDate(cal.get(Calendar.YEAR), 12, 31);

        return Days.daysBetween(datum, jahresEnde).getDays();
    }

    private static String erstattungBerechnen(double versPraemie, int versDauer, int versRestLfz) {

        double ergebnis = versPraemie - ((versPraemie * versDauer) / versRestLfz);

        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
        dfs.setDecimalSeparator(',');
        DecimalFormat dFormat = new DecimalFormat("0.00", dfs);

        return dFormat.format(ergebnis);

    }
    private static String tausenderZeichen(String eingabe) {

        DecimalFormat df = (DecimalFormat) (DecimalFormat.getInstance(Locale.GERMAN));
        DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        df.setDecimalFormatSymbols(symbols);

        return df.format(Double.parseDouble(eingabe.replace(',', '.')));
    }

    @FXML
    public void berechnenButton(ActionEvent e) {

        LocalDate postEingang;
        LocalDate auszahlung;
        int versDauer;
        int versRestLfz;

        StringTokenizer st1 = new StringTokenizer(postEingangsDatum.getEditor().getText());
        StringTokenizer st2 = new StringTokenizer(auszahlungsDatum.getEditor().getText());

        String token;
        Boolean b = false;

        while (st1.hasMoreTokens()) {
            token = st1.nextToken();
            if (token.matches("[A-Za-z]"))
                b = true;
        }

        while (st2.hasMoreTokens()) {
            token = st2.nextToken();
            if (token.matches("[A-Za-z]"))
                b = true;
        }

        if (postEingangsDatum.getEditor().getText().isEmpty() || auszahlungsDatum.getEditor().getText().isEmpty() || versPraemie.getText().isEmpty() || b)
            eingabeFehlerAlert();

        else {

            double versPraemieDouble = Double.parseDouble(versPraemie.getText().replace(',', '.'));

            if (datumAusEingabe(postEingangsDatum.getEditor().getText()).toString().equals("9999-12-31"))
                datumFehlerAlert();
            else if (datumAusEingabe(auszahlungsDatum.getEditor().getText()).toString().equals("9999-12-31"))
                datumFehlerAlert();
            else {
                postEingang = datumAusEingabe(postEingangsDatum.getEditor().getText());
                auszahlung = datumAusEingabe(auszahlungsDatum.getEditor().getText());
                versDauer = restTageSonstige(auszahlung, postEingang);
                versRestLfz = restTageJE(auszahlung);
                ergbnisClipboard = erstattungBerechnen(versPraemieDouble, versDauer, versRestLfz);

                ergebnis.setText(tausenderZeichen(ergbnisClipboard));


                if (versDauer == 1)
                    versDauerErgebnis.setText(String.format("Versicherungsdauer: %d Tag", versDauer));
                else
                    versDauerErgebnis.setText(String.format("Versicherungsdauer: %d Tage", versDauer));

                if (versRestLfz == 1)
                    versRestErgebnis.setText(String.format("Versicherungsrestlaufzeit: %d Tag", versRestLfz));
                else
                    versRestErgebnis.setText(String.format("Versicherungsrestlaufzeit: %d Tage", versRestLfz));
            }
        }
    }

    private static void datumFehlerAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText("Datumsformat falsch");
        alert.setContentText("Bitte geben Sie das Datum im Format TT.MM.JJJJ ein oder wählen Sie einen Tag über den Kalender aus.");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    private static void eingabeFehlerAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Achtung");
        alert.setHeaderText("Angaben nicht vollständig oder falsch!");
        alert.setContentText("Bitte geben Sie ein gültiges Posteingangsdatum, Auszahlungsdatum sowie eine Versicherungsprämie ein.");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    @FXML
    public void kopierenButton(ActionEvent e) {

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();

        content.putString(ergbnisClipboard);
        clipboard.setContent(content);
    }

    @FXML
    public void ueberMenuItem(ActionEvent e) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Über...");
        alert.setHeaderText("Version: 1.0 Build 20180622");
        alert.setContentText("Chris Merscher \nLBS Ostdeutsche Landesbausparkasse AG \n71-30 Anwendungsbetreuung und -entwicklung");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    @FXML
    public void exitItem(ActionEvent e) {
        Platform.exit();
    }

}