package labirent;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import labirent.LabirentModeli.HucreDurumu;

public class LabirentGörünüm extends Group
{
    public final static double HUCRE_GENISLIGI = 40;


    @FXML
    private int satirSayisi;
    @FXML
    private int sutunSayisi;

    private ImageView[][] hucreGorunumleri;

    private Image buyucuYukariImage;
    private Image buyucuAsagiImage;
    private Image buyucuSolImage;
    private Image buyucuSagImage;

    private Image canavarImage;

    private Image elmasImage;
    private Image buyukElmasImage;
    private Image duvarImage;
    private Image kutuImage;
    private Image cikisImage;
    private Image gucImage;

    public LabirentGörünüm()
    {
        this.buyucuAsagiImage = new Image(getClass().getResourceAsStream("/gorseller/buyucu_asagi.gif"));
        this.buyucuYukariImage = new Image(getClass().getResourceAsStream("/gorseller/buyucu_sag.gif"));
        this.buyucuSolImage = new Image(getClass().getResourceAsStream("/gorseller/buyucu_sol.gif"));
        this.buyucuSagImage = new Image(getClass().getResourceAsStream("/gorseller/buyucu_yukari.gif"));

        this.canavarImage = new Image(getClass().getResourceAsStream("/gorseller/monster.png"));

        this.elmasImage = new Image(getClass().getResourceAsStream("/gorseller/diamond.png"));
        this.buyukElmasImage = new Image(getClass().getResourceAsStream("/gorseller/big_diamond.gif"));
        this.duvarImage = new Image(getClass().getResourceAsStream("/gorseller/agac.png"));
        this.kutuImage = new Image(getClass().getResourceAsStream("/gorseller/kutu.png"));
        this.cikisImage = new Image(getClass().getResourceAsStream("/gorseller/cikis.png"));
        this.gucImage = new Image(getClass().getResourceAsStream("/gorseller/power.png"));
    }

    private void hucreleriYukle()
    {
        if (this.satirSayisi > 0 && this.sutunSayisi > 0)
        {
            this.hucreGorunumleri = new ImageView[this.satirSayisi][this.sutunSayisi];
            for (int row = 0; row < this.satirSayisi; row++)
            {
                for (int column = 0; column < this.sutunSayisi; column++)
                {
                    ImageView imageView = new ImageView();
                    imageView.setX((double)column * HUCRE_GENISLIGI);
                    imageView.setY((double)row * HUCRE_GENISLIGI);
                    imageView.setFitWidth(HUCRE_GENISLIGI);
                    imageView.setFitHeight(HUCRE_GENISLIGI);
                    this.hucreGorunumleri[row][column] = imageView;
                    this.getChildren().add(imageView);
                }
            }
        }
    }

    public void guncelle(LabirentModeli model)
    {
        assert model.getSatirSayisi() == this.satirSayisi && model.getSutunSayisi() == this.sutunSayisi;

        for (int satir = 0; satir < this.satirSayisi; satir++)
        {
            for (int sutun = 0; sutun < this.sutunSayisi; sutun++)
            {
                HucreDurumu deger = model.getHucreDurumu(satir, sutun);
                if (deger == HucreDurumu.DUVAR)
                {
                    this.hucreGorunumleri[satir][sutun].setImage(this.duvarImage);
                }
                else if (deger == HucreDurumu.ELMAS)
                {
                    this.hucreGorunumleri[satir][sutun].setImage(this.elmasImage);
                }
                else if (deger == HucreDurumu.GUC)
                {
                    this.hucreGorunumleri[satir][sutun].setImage(this.gucImage);
                }
                else if (deger == HucreDurumu.BUYUK_ELMAS)
                {
                    this.hucreGorunumleri[satir][sutun].setImage(this.buyukElmasImage);
                }
                else if (deger == HucreDurumu.KUTU)
                {
                    this.hucreGorunumleri[satir][sutun].setImage(this.kutuImage);
                }
                else if (deger == HucreDurumu.CIKIS)
                {
                    this.hucreGorunumleri[satir][sutun].setImage(this.cikisImage);
                }
                else
                {
                    this.hucreGorunumleri[satir][sutun].setImage(null);
                }

                // Büyücü hareket animasyonları
                // Eğer büyücü hareketsiz ise, buyucuSagImage animasyonu çalışıyor.
                if (satir == model.getBuyucuKonumu().getX() &&
                        sutun == model.getBuyucuKonumu().getY() &&
                        (LabirentModeli.getSonYon() == LabirentModeli.Yon.SAG ||
                                LabirentModeli.getSonYon() == LabirentModeli.Yon.NONE))
                {
                    this.hucreGorunumleri[satir][sutun].setImage(this.buyucuSagImage);
                }
                if (satir == model.getBuyucuKonumu().getX() &&
                        sutun == model.getBuyucuKonumu().getY() &&
                        (LabirentModeli.getSonYon() == LabirentModeli.Yon.SOL))
                {
                    this.hucreGorunumleri[satir][sutun].setImage(this.buyucuSolImage);
                }
                if (satir == model.getBuyucuKonumu().getX() &&
                        sutun == model.getBuyucuKonumu().getY() &&
                        (LabirentModeli.getSonYon() == LabirentModeli.Yon.YUKARI))
                {
                    this.hucreGorunumleri[satir][sutun].setImage(this.buyucuYukariImage);
                }
                if (satir == model.getBuyucuKonumu().getX() &&
                        sutun == model.getBuyucuKonumu().getY() &&
                        (LabirentModeli.getSonYon() == LabirentModeli.Yon.ASAGI))
                {
                    this.hucreGorunumleri[satir][sutun].setImage(this.buyucuAsagiImage);
                }

                if (LabirentModeli.isGucluModu() && (Controller.getgucluModuSayac() == 6 ||Controller.getgucluModuSayac() == 4 || Controller.getgucluModuSayac() == 2)) {
                    if (satir == LabirentModeli.getDusman1Konumu().getX() && sutun == model.getDusman1Konumu().getY()) {
                        this.hucreGorunumleri[satir][sutun].setImage(this.canavarImage);
                    }
                    if (satir == LabirentModeli.getDusman2Konumu().getX() && sutun == model.getDusman2Konumu().getY()) {
                        this.hucreGorunumleri[satir][sutun].setImage(this.canavarImage);
                    }
                }
                else {
                    if (satir == LabirentModeli.getDusman1Konumu().getX() && sutun == model.getDusman1Konumu().getY()) {
                        this.hucreGorunumleri[satir][sutun].setImage(this.canavarImage);
                    }
                    if (satir == LabirentModeli.getDusman2Konumu().getX() && sutun == model.getDusman2Konumu().getY()) {
                        this.hucreGorunumleri[satir][sutun].setImage(this.canavarImage);
                    }
                }
            }
        }
    }

    public int getSatirSayisi()
    {
        return this.satirSayisi;
    }

    public void setSatirSayisi(int satirSayisi)
    {
        this.satirSayisi = satirSayisi;
        this.hucreleriYukle();
    }

    public int getSutunSayisi()
    {
        return this.sutunSayisi;
    }

    public void setSutunSayisi(int sutunSayisi)
    {
        this.sutunSayisi = sutunSayisi;
        this.hucreleriYukle();
    }
}
