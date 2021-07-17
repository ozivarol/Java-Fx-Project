package labirent;

import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;

public class Controller implements EventHandler<KeyEvent>
{
    private LabirentModeli labirentModeli;

    @FXML
    private Label skorLabel;
    @FXML
    private Label bolumLabel;
    @FXML
    private Label oyunBittiLabel;
    @FXML
    private LabirentGörünüm labirentGörünüm;

    private static final String[] bolumDosyalari = {"src/bolumler/bolum1.txt", "src/bolumler/bolum2.txt", "src/bolumler/bolum3.txt"};

    private static int gucluModuSayac;

    private boolean durduruldu;

    public Controller()
    {
        this.durduruldu = false;
    }

    public void initialize()
    {
        String file = this.getBolumDosyasi(0);
        this.labirentModeli = new LabirentModeli();
        gucluModuSayac = 25;
        this.yonuGuncelle(LabirentModeli.Yon.NONE);
    }

    private void yonuGuncelle(LabirentModeli.Yon yon)
    {
        this.labirentModeli.adim(yon);
        this.labirentGörünüm.guncelle(labirentModeli);
        this.bolumLabel.setText(String.format("Bolum: %d", this.labirentModeli.getBolum()));
        this.skorLabel.setText(String.format("Skor: %d", this.labirentModeli.getSkor()));
        if (labirentModeli.isOyunBitti()) {
            this.oyunBittiLabel.setText(String.format("Kaybettin :("));
            durdur();
        }
        if (labirentModeli.isKazandin())
        {
            this.oyunBittiLabel.setText(String.format("KAZANDIN!"));
            durdur();
        }

        if (labirentModeli.isGucluModu()) {
            gucluModuSayac--;
        }
        if (gucluModuSayac == 0 && labirentModeli.isGucluModu()) {
            labirentModeli.setGucluModu(false);
        }
    }

    @Override
    public void handle(KeyEvent keyEvent)
    {
        boolean tusMevcut = true;
        KeyCode code = keyEvent.getCode();
        LabirentModeli.Yon yon = LabirentModeli.Yon.NONE;

        if (code == KeyCode.A)
        {
            yon = LabirentModeli.Yon.SOL;
        }

        else if (code == KeyCode.D)
        {
            yon = LabirentModeli.Yon.SAG;
        }

        else if (code == KeyCode.W)
        {
            yon = LabirentModeli.Yon.YUKARI;
        }

        else if (code == KeyCode.S)
        {
            yon = LabirentModeli.Yon.ASAGI;
        }

        else if (code == KeyCode.P)
        {
            durdur();
            this.labirentModeli.yeniOyunBaslat();
            this.oyunBittiLabel.setText(String.format(""));
            durduruldu = false;
        }
        else
        {
            tusMevcut = false;
        }

        if (tusMevcut)
        {
            keyEvent.consume();
            labirentModeli.setAnlikYon(yon);
            yonuGuncelle(labirentModeli.getAnlikYon());
        }

    }


    public void durdur()
    {
        this.durduruldu = true;
    }

    public static String getBolumDosyasi(int x)
    {
        return bolumDosyalari[x];
    }

    public boolean getDurduruldu()
    {
        return durduruldu;
    }

    public double getSahneGenisligi()
    {
        return labirentGörünüm.HUCRE_GENISLIGI * this.labirentGörünüm.getSutunSayisi();
    }

    public double getSahneYuksekligi()
    {
        return labirentGörünüm.HUCRE_GENISLIGI * this.labirentGörünüm.getSatirSayisi();
    }

    public static void setgucluModuSayac() {
        gucluModuSayac = 25;
    }

    public static int getgucluModuSayac() {
        return gucluModuSayac;
    }

}
