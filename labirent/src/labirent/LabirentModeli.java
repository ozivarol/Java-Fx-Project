package labirent;

import java.awt.*;
import java.io.*;
import java.time.Year;
import java.util.*;
import javafx.geometry.Point2D;
import javafx.fxml.FXML;

public class LabirentModeli
{
    public LabirentModeli()
    {
        this.yeniOyunBaslat();
    }

    public enum Yon
    {
        YUKARI,
        ASAGI,
        SAG,
        SOL,
        NONE
    }
    public enum HucreDurumu
    {
        BOS,
        ELMAS,
        BUYUK_ELMAS,
        DUVAR,
        KUTU,
        OYUNCU,
        CIKIS,
        DUSMAN1,
        DUSMAN2,
        KOSMA,
        GUC,
        FIRLATMA
    }

    @FXML
    private int satirSayisi;
    @FXML
    private int sutunSayisi;

    private HucreDurumu[][] hucre;

    private ArrayList<Integer> BosHucreler;

    private int skor;
    private int bolum;
    private int elmasSayisi;

    private static boolean oyunBitti;
    private static boolean kazandin;

    private static boolean kosmaModu;
    private static boolean gucluModu;
    private static boolean firlatmaModu;

    private Point2D buyucuKonumu;
    private static Point2D dusman1Konumu;
    private static Point2D dusman2Konumu;
    private Point2D dusman1Momentumu;
    private Point2D dusman2Momentumu;


    private static Yon sonYon;
    private static Yon anlikYon;

    public void bolumuYukle(String dosyaAdi)
    {
        File dosya = new File(dosyaAdi);
        Scanner scanner = null;
        try
        {
            scanner = new Scanner(dosya);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            while (lineScanner.hasNext())
            {
                lineScanner.next();
                sutunSayisi++;
            }
            satirSayisi++;
        }

        sutunSayisi = sutunSayisi/satirSayisi;
        Scanner scanner2 = null;
        try
        {
            scanner2 = new Scanner(dosya);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        hucre = new HucreDurumu[satirSayisi][sutunSayisi];
        int satir = 0;
        int oyuncuSatir = 0;
        int oyuncuSutun = 0;
        int dusman1Satir = 0;
        int dusman1Sutun = 0;
        int dusman2Satir = 0;
        int dusman2Sutun = 0;

        while (scanner2.hasNextLine())
        {
            int sutun = 0;
            String line = scanner2.nextLine();
            Scanner lineScanner = new Scanner(line);
            while (lineScanner.hasNext())
            {
                String value = lineScanner.next();
                HucreDurumu hucreDegeri;
                if (value.equals("D"))
                {
                    hucreDegeri = HucreDurumu.DUVAR;
                }
                else if (value.equals("E"))
                {
                    hucreDegeri = HucreDurumu.ELMAS;
                    elmasSayisi++;
                }
                else if (value.equals("B"))
                {
                    hucreDegeri = HucreDurumu.BUYUK_ELMAS;
                    elmasSayisi++;
                }
                else if (value.equals("K"))
                {
                    hucreDegeri = HucreDurumu.KUTU;
                }
                else if (value.equals("1")){
                    hucreDegeri = HucreDurumu.DUSMAN1;
                    dusman1Satir = satir;
                    dusman1Sutun = sutun;
                }
                else if (value.equals("2")){
                    hucreDegeri = HucreDurumu.DUSMAN2;
                    dusman2Satir = satir;
                    dusman2Sutun = sutun;
                }
                else if (value.equals("O"))
                {
                    hucreDegeri = HucreDurumu.OYUNCU;
                    oyuncuSatir = satir;
                    oyuncuSutun = sutun;
                }
                else if (value.equals("G"))
                {
                    hucreDegeri = HucreDurumu.GUC;
                }
                else if (value.equals("C"))
                {
                    hucreDegeri = HucreDurumu.CIKIS;
                }
                else
                {
                    hucreDegeri = HucreDurumu.BOS;
                }
                hucre[satir][sutun] = hucreDegeri;
                sutun++;
            }
            satir ++;
        }
        buyucuKonumu = new Point2D(oyuncuSatir, oyuncuSutun);
        dusman1Konumu = new Point2D(dusman1Satir, dusman1Sutun);
        dusman1Momentumu = new Point2D(-1, 0);

        dusman2Konumu = new Point2D(dusman2Satir, dusman2Sutun);
        dusman2Momentumu = new Point2D(-1, 0);
        anlikYon = Yon.NONE;
        sonYon = Yon.NONE;
    }

    public void yeniOyunBaslat()
    {
        this.oyunBitti = false;
        this.kazandin = false;
        elmasSayisi = 0;
        satirSayisi = 0;
        sutunSayisi = 0;
        kosmaModu = false;
        gucluModu = false;
        firlatmaModu = false;
        this.skor = 0;
        this.bolum = 1;
        this.bolumuYukle(Controller.getBolumDosyasi(0));
    }

    public void sonrakiBolumuYukle()
    {
        if (this.isBolumBitti())
        {
            this.bolum++;
            satirSayisi = 0;
            sutunSayisi = 0;
            kazandin = false;
            kosmaModu = false;
            gucluModu = false;
            firlatmaModu = false;
            try
            {
                this.bolumuYukle(Controller.getBolumDosyasi(bolum - 1));
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                // eğer oynanacak bölüm kalmadıysa oyunu bitir.
                kazandin = true;
                oyunBitti = true;
                bolum--;
            }
        }
    }

    public void adim(Yon yon)
    {
        this.oyuncuyuOynat(yon);

        HucreDurumu buyucuKonumuHucreDurumu = hucre[(int) buyucuKonumu.getX()][(int) buyucuKonumu.getY()];

        // Eğer büyücü bir elmasın üzerinde ise, elması kaldır.
        if (buyucuKonumuHucreDurumu == HucreDurumu.ELMAS)
        {
            hucre[(int) buyucuKonumu.getX()][(int) buyucuKonumu.getY()] = HucreDurumu.BOS;
            elmasSayisi--;
            skor += 1;
        }
        else if (buyucuKonumuHucreDurumu == HucreDurumu.BUYUK_ELMAS)
        {
            hucre[(int) buyucuKonumu.getX()][(int) buyucuKonumu.getY()] = HucreDurumu.BOS;
            elmasSayisi--;
            skor += 10;
        }
        if (buyucuKonumuHucreDurumu == HucreDurumu.GUC) {
            hucre[(int) buyucuKonumu.getX()][(int) buyucuKonumu.getY()] = HucreDurumu.BOS;
            skor += 50;
            gucluModu = true;
            Controller.setgucluModuSayac();
        }
        // Eğer büyücü bir kutunun olduğu kareye geliyorsa, kutuyu bir sonraki kareye ittir.
        // Bir sonraki kare duvar veya çıkış ise, dur. Kutu duvarlardan geçemez ve çıkışı kapatamaz.
        if ((buyucuKonumuHucreDurumu == HucreDurumu.KUTU))
        {
            Point2D yeniKutuPozisyonu = KutuPozisyonuDegistir(yon, (int)buyucuKonumu.getX(), (int)buyucuKonumu.getY());
            if (hucre[(int)yeniKutuPozisyonu.getX()][(int)yeniKutuPozisyonu.getY()] == HucreDurumu.DUVAR)
            {
                setAnlikYon(Yon.NONE);
                setSonYon(Yon.NONE);
            }
            else if (hucre[(int)yeniKutuPozisyonu.getX()][(int)yeniKutuPozisyonu.getY()] == HucreDurumu.CIKIS)
            {
                setAnlikYon(Yon.NONE);
                setSonYon(Yon.NONE);
            }
            else
            {
                hucre[(int) buyucuKonumu.getX()][(int) buyucuKonumu.getY()] = HucreDurumu.BOS;
                hucre[(int) yeniKutuPozisyonu.getX()][(int) yeniKutuPozisyonu.getY()] = HucreDurumu.KUTU;
            }
        }

        if (gucluModu)
        {
            if (buyucuKonumu.equals(dusman1Konumu)) {
                hucre[(int) dusman1Konumu.getX()][(int) dusman1Konumu.getY()] = HucreDurumu.BOS;
                skor += 100;
            }
            if (buyucuKonumu.equals(dusman2Konumu)) {
                hucre[(int) dusman2Konumu.getX()][(int) dusman2Konumu.getY()] = HucreDurumu.BOS;
                skor += 100;
            }
        }
        else
        {
            if (buyucuKonumu.equals(dusman1Konumu)) {
                oyunBitti = true;
            }
            if (buyucuKonumu.equals(dusman2Konumu)) {
                oyunBitti = true;
            }
        }
        this.dusmanlariHareketEttir();
        // Düşmanları hareket ettir ve tekrar kontrol et.
        if (gucluModu)
        {
            if (buyucuKonumu.equals(dusman1Konumu)) {
                Dusman1YerineGonder();
                skor += 100;
            }
            if (buyucuKonumu.equals(dusman2Konumu)) {
                Dusman2YerineGonder();
                skor += 100;
            }
        }
        else
        {
            if (buyucuKonumu.equals(dusman1Konumu)) {
                oyunBitti = true;
            }
            if (buyucuKonumu.equals(dusman2Konumu)) {
                oyunBitti = true;
            }
        }

        if (this.isBolumBitti())
        {
            sonrakiBolumuYukle();
        }
    }

    public void oyuncuyuOynat(Yon yon)
    {
        Point2D potansiyelBuyucuMomentumu = MomentumDegistir(yon);
        Point2D potansiyelBuyucuKonumu = buyucuKonumu.add(potansiyelBuyucuMomentumu);

        // eğer büyücü harita dışına çıkmaya çalışıyorsa geri döndür.
        potansiyelBuyucuKonumu = HaritaDisinaCikanPozisyon(potansiyelBuyucuKonumu);

        // Büyücünün düz devam edip etmemesini belirliyoruz.
        // Eğer son basılan tuş, ondan önceki tuşla aynıysa, duvar olup olmadığını kontrol ediyoruz.

        if (yon.equals(sonYon))
        {
            Point2D yeniKutuPozisyonu = KutuPozisyonuDegistir(yon, (int)potansiyelBuyucuKonumu.getX(), (int)potansiyelBuyucuKonumu.getY());
            // Eğer aynı yönde ilerlemek duvara çarpmaya sebep olduysa, dur.
            if (hucre[(int) potansiyelBuyucuKonumu.getX()][(int) potansiyelBuyucuKonumu.getY()] == HucreDurumu.DUVAR)
            {
                setSonYon(Yon.NONE);
            }
            // Eğer aynı yönde ilerlemek kutuyu duvara çıkaracak ise, dur.
            else if ((hucre[(int) potansiyelBuyucuKonumu.getX()][(int) potansiyelBuyucuKonumu.getY()] == HucreDurumu.KUTU) &&
                    ((hucre[(int) yeniKutuPozisyonu.getX()][(int) yeniKutuPozisyonu.getY()] == HucreDurumu.DUVAR)))
            {
                setAnlikYon(Yon.NONE);
                setSonYon(Yon.NONE);
            }
            // Eğer aynı yönde ilerlemek kutunun çıkış karesini kapatmasını sağlayacak ise, dur.
            else if ((hucre[(int) potansiyelBuyucuKonumu.getX()][(int) potansiyelBuyucuKonumu.getY()] == HucreDurumu.KUTU) &&
                    ((hucre[(int) yeniKutuPozisyonu.getX()][(int) yeniKutuPozisyonu.getY()] == HucreDurumu.CIKIS)))
            {
                setAnlikYon(Yon.NONE);
                setSonYon(Yon.NONE);
            }
            else
            {
                buyucuKonumu = potansiyelBuyucuKonumu;
            }
        }
        // Eğer son yön, ondan önceki yön ile aynı değilse, o yöne sapmadan önce duvarları ve çıkışı kontrol et.
        else
        {
            Point2D yeniKutuPozisyonu = KutuPozisyonuDegistir(yon, (int)potansiyelBuyucuKonumu.getX(), (int)potansiyelBuyucuKonumu.getY());
            // Eğer büyücü yeni yön ile bir duvara çarpacak ise, önceki yöne dönmeden önce bir duvar olmadığından emin ol.
            if (hucre[(int) potansiyelBuyucuKonumu.getX()][(int) potansiyelBuyucuKonumu.getY()] == HucreDurumu.DUVAR)
            {
                potansiyelBuyucuMomentumu = MomentumDegistir(sonYon);
                potansiyelBuyucuKonumu = buyucuKonumu.add(potansiyelBuyucuMomentumu);
                // Eğer dönülen öteki yön duvara çarpıyorsa, hareketi durdur.
                if (hucre[(int) potansiyelBuyucuKonumu.getX()][(int) potansiyelBuyucuKonumu.getY()] == HucreDurumu.DUVAR)
                {
                    setSonYon(Yon.NONE);
                }
            }
            // Eğer büyücü yeni yön bile bir kutuya çarpıyorsa, ancak kutunun yeni pozisyonu duvara denk geliyorsa, dur.
            else if (hucre[(int) potansiyelBuyucuKonumu.getX()][(int) potansiyelBuyucuKonumu.getY()] == HucreDurumu.KUTU &&
                    hucre[(int) yeniKutuPozisyonu.getX()][(int) yeniKutuPozisyonu.getY()] == HucreDurumu.DUVAR)
            {
                setSonYon(Yon.NONE);
            }
            else if (hucre[(int) potansiyelBuyucuKonumu.getX()][(int) potansiyelBuyucuKonumu.getY()] == HucreDurumu.KUTU &&
                    hucre[(int) yeniKutuPozisyonu.getX()][(int) yeniKutuPozisyonu.getY()] == HucreDurumu.CIKIS)
            {
                setSonYon(Yon.NONE);
            }
            // sorun yoksa yönü değiştir ve harekete devam et.
            else
            {
                buyucuKonumu = potansiyelBuyucuKonumu;
                setSonYon(yon);
            }
        }
    }

    public void dusmanlariHareketEttir() {
        Point2D[] ghost1Data = dusmaniOynat(dusman1Momentumu, dusman1Konumu);
        Point2D[] ghost2Data = dusmaniOynat(dusman2Momentumu, dusman2Konumu);
        dusman1Momentumu = ghost1Data[0];
        dusman1Konumu = ghost1Data[1];
        dusman2Momentumu = ghost2Data[0];
        dusman2Konumu = ghost2Data[1];

    }

    public Point2D[] dusmaniOynat(Point2D momentum, Point2D konum) {
        Random generator = new Random();

        if (!gucluModu) {
            // düşman büyücünün sütununda mı kontrol et, öyleyse üzerine yürü
            if (konum.getY() == buyucuKonumu.getY()) {
                if (konum.getX() > buyucuKonumu.getX()) {
                    momentum = MomentumDegistir(Yon.YUKARI);
                } else {
                    momentum = MomentumDegistir(Yon.ASAGI);
                }
                Point2D potentialLocation = konum.add(momentum);
                // ekran dışına çıkacaksa, düzelt
                potentialLocation = HaritaDisinaCikanPozisyon(potentialLocation);
                // düşmanlar duvarlara çarpmayacak şekilde rastgele yürüme pozisyonları oluştur
                while (hucre[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == HucreDurumu.DUVAR) {
                    int randomNum = generator.nextInt(4);
                    Yon direction = intToYon(randomNum);
                    momentum = MomentumDegistir(direction);
                    potentialLocation = konum.add(momentum);
                }
                konum = potentialLocation;
            }
            // düşman büyücünün satırında mı kontrol et, öyleyse üzerine yürü
            else if (konum.getX() == buyucuKonumu.getX()) {
                if (konum.getY() > buyucuKonumu.getY()) {
                    momentum = MomentumDegistir(Yon.SOL);
                } else {
                    momentum = MomentumDegistir(Yon.SAG);
                }
                Point2D potentialLocation = konum.add(momentum);
                potentialLocation = HaritaDisinaCikanPozisyon(potentialLocation);
                while (hucre[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == HucreDurumu.DUVAR) {
                    int randomNum = generator.nextInt(4);
                    Yon direction = intToYon(randomNum);
                    momentum = MomentumDegistir(direction);
                    potentialLocation = konum.add(momentum);
                }
                konum = potentialLocation;
            }
            // sabit bir rastgele pozisyonda duvara çarpana kadar yürü, sonra yeni bir rastgele yön seç
            else {
                Point2D potentialLocation = konum.add(momentum);
                potentialLocation = HaritaDisinaCikanPozisyon(potentialLocation);
                while (hucre[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == HucreDurumu.DUVAR) {
                    int randomNum = generator.nextInt(4);
                    Yon direction = intToYon(randomNum);
                    momentum = MomentumDegistir(direction);
                    potentialLocation = konum.add(momentum);
                }
                konum = potentialLocation;
            }
        }
        if (gucluModu) {
            if (konum.getY() == buyucuKonumu.getY()) {
                if (konum.getX() > buyucuKonumu.getX()) {
                    momentum = MomentumDegistir(Yon.ASAGI);
                } else {
                    momentum = MomentumDegistir(Yon.YUKARI);
                }
                Point2D potentialLocation = konum.add(momentum);
                potentialLocation = HaritaDisinaCikanPozisyon(potentialLocation);
                while (hucre[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == HucreDurumu.DUVAR) {
                    int randomNum = generator.nextInt(4);
                    Yon direction = intToYon(randomNum);
                    momentum = MomentumDegistir(direction);
                    potentialLocation = konum.add(momentum);
                }
                konum = potentialLocation;
            } else if (konum.getX() == buyucuKonumu.getX()) {
                if (konum.getY() > buyucuKonumu.getY()) {
                    momentum = MomentumDegistir(Yon.SAG);
                } else {
                    momentum = MomentumDegistir(Yon.SOL);
                }
                Point2D potentialLocation = konum.add(momentum);
                potentialLocation = HaritaDisinaCikanPozisyon(potentialLocation);
                while (hucre[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == HucreDurumu.DUVAR) {
                    int randomNum = generator.nextInt(4);
                    Yon direction = intToYon(randomNum);
                    momentum = MomentumDegistir(direction);
                    potentialLocation = konum.add(momentum);
                }
                konum = potentialLocation;
            }
            else{
                Point2D potentialLocation = konum.add(momentum);
                potentialLocation = HaritaDisinaCikanPozisyon(potentialLocation);
                while(hucre[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == HucreDurumu.DUVAR){
                    int randomNum = generator.nextInt( 4);
                    Yon direction = intToYon(randomNum);
                    momentum = MomentumDegistir(direction);
                    potentialLocation = konum.add(momentum);
                }
                konum = potentialLocation;
            }
        }
        Point2D[] data = {momentum, konum};
        return data;
    }

        public Point2D HaritaDisinaCikanPozisyon(Point2D buyucu)
    {
        // eger buyucu sağ taraftan dışarıya çıkmaya çalışıyorsa
        if (buyucu.getY() >= sutunSayisi) {
            buyucu = new Point2D(buyucu.getX(), 0);
        }
        // eğer sol taraftan çıkmaya çalışıyorsa
        if (buyucu.getY() < 0) {
            buyucu = new Point2D(buyucu.getX(), sutunSayisi - 1);
        }
        return buyucu;
    }

    public void Dusman1YerineGonder() {
        for (int satir = 0; satir < this.satirSayisi; satir++) {
            for (int sutun = 0; sutun < this.sutunSayisi; sutun++) {
                if (hucre[satir][sutun] == HucreDurumu.DUSMAN1) {
                    dusman1Konumu = new Point2D(satir, sutun);
                }
            }
        }
        dusman1Momentumu = new Point2D(-1, 0);
    }

    public void Dusman2YerineGonder() {
        for (int satir = 0; satir < this.satirSayisi; satir++) {
            for (int sutun = 0; sutun < this.sutunSayisi; sutun++) {
                if (hucre[satir][sutun] == HucreDurumu.DUSMAN2) {
                    dusman2Konumu = new Point2D(satir, sutun);
                }
            }
        }
        dusman2Momentumu = new Point2D(-1, 0);
    }

    public static Point2D getDusman1Konumu() {
        return dusman1Konumu;
    }

    public void setDusman1Konumu(Point2D dusman1Konumu) {
        this.dusman1Konumu = dusman1Konumu;
    }

    public static Point2D getDusman2Konumu() {
        return dusman2Konumu;
    }

    public Point2D getDusman1Momentumu() {
        return dusman1Momentumu;
    }

    public Point2D getDusman2Momentumu() {
        return dusman2Momentumu;
    }

    public Yon intToYon(int x){
        if (x == 0){
            return Yon.SOL;
        }
        else if (x == 1){
            return Yon.SAG;
        }
        else if(x == 2){
            return Yon.YUKARI;
        }
        else{
            return Yon.ASAGI;
        }
    }

    public int getBolum()
    {
        return bolum;
    }

    public void setBolum(int bolum)
    {
        this.bolum = bolum;
    }

    public static boolean isKazandin()
    {
        return kazandin;
    }

    public static boolean isOyunBitti()
    {
        return oyunBitti;
    }

    public int getSkor()
    {
        return skor;
    }

    public void setSkor(int skor)
    {
        this.skor = skor;
    }

    public void skoraEkle(int puan)
    {
        this.skor += puan;
    }

    public static Yon getSonYon()
    {
        return sonYon;
    }

    public void setSonYon(Yon yon)
    {
        sonYon = yon;
    }

    public static Yon getAnlikYon()
    {
        return anlikYon;
    }

    public void setAnlikYon(Yon yon)
    {
        anlikYon = yon;
    }

    public int getSatirSayisi()
    {
        return satirSayisi;
    }

    public void setSatirSayisi(int satirSayisi)
    {
        this.satirSayisi = satirSayisi;
    }

    public int getSutunSayisi()
    {
        return sutunSayisi;
    }

    public void setSutunSayisi(int sutunSayisi)
    {
        this.sutunSayisi = sutunSayisi;
    }

    public static boolean isKosmaModu() {
        return kosmaModu;
    }

    public static void setKosmaModu(boolean kosmaModu) {
        LabirentModeli.kosmaModu = kosmaModu;
    }

    public static boolean isGucluModu() {
        return gucluModu;
    }

    public static void setGucluModu(boolean gucluModu) {
        LabirentModeli.gucluModu = gucluModu;
    }

    public static boolean isFirlatmaModu() {
        return firlatmaModu;
    }

    public static void setFirlatmaModu(boolean firlatmaModu) {
        LabirentModeli.firlatmaModu = firlatmaModu;
    }

    public Point2D getBuyucuKonumu()
    {
        return buyucuKonumu;
    }

    public void setBuyucuKonumu(Point2D buyucuKonumu)
    {
        this.buyucuKonumu = buyucuKonumu;
    }

    public HucreDurumu getHucreDurumu(int satir, int sutun)
    {
        assert satir >= 0 && satir < this.hucre.length && sutun >= 0 && sutun < this.hucre[0].length;
        return this.hucre[satir][sutun];
    }

    public Point2D MomentumDegistir(Yon yon)
    {
        if(yon == Yon.SOL){
            return new Point2D(0,-1);
        }
        else if(yon == Yon.SAG){
            return new Point2D(0,1);
        }
        else if(yon == Yon.YUKARI){
            return new Point2D(-1,0);
        }
        else if(yon == Yon.ASAGI){
            return new Point2D(1,0);
        }
        else{
            return new Point2D(0,0);
        }
    }

    public Point2D KutuPozisyonuDegistir(Yon yon, int konumX, int konumY)
    {
        if(yon == Yon.SOL)
        {
            return new Point2D(konumX + 0, konumY-1);
        }
        else if(yon == Yon.SAG)
        {
            return new Point2D(konumX+0,konumY+1);
        }
        else if(yon == Yon.YUKARI)
        {
            return new Point2D(konumX-1,konumY+0);
        }
        else if(yon == Yon.ASAGI)
        {
            return new Point2D(konumX+1,konumY+0);
        }
        else
        {
            return new Point2D(konumX+0,konumY+0);
        }
    }
    public boolean isBolumBitti()
    {
        HucreDurumu buyucuKonumuHucreDurumu = hucre[(int) buyucuKonumu.getX()][(int) buyucuKonumu.getY()];
        return /*this.elmasSayisi == 0 &&*/ buyucuKonumuHucreDurumu == HucreDurumu.CIKIS;
    }
}
