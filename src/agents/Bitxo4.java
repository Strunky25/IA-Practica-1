

// 
// Decompiled by Procyon v0.5.36
// 

package agents;

import java.util.Random;

public class Bitxo4 extends Agent
{
    static final int PARET = 0;
    static final int NAU = 1;
    static final int RES = -1;
    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;
    static final int MAXDIST = 9999;
    Random random;
    Estat estat;
    int espera;
    int tempsEnCollisio;
    boolean mHanFerit;
    int ferides;
    long temps;
    long tempsDarreraFerida;
    
    public Bitxo4(final Agents pare) {
        super(pare, "Exemple4", "imatges/robotank4.gif");
        this.random = new Random();
        this.espera = 0;
        this.tempsEnCollisio = 0;
        this.mHanFerit = false;
        this.ferides = 0;
    }
    
    public void inicia() {
        final int cost = this.atributsAgent(6, 5, 600, 30, 23, 5, 5);
        System.out.println("Cost total:" + cost);
        this.espera = 0;
        this.temps = 0L;
        this.tempsDarreraFerida = 0L;
        this.ferides = 0;
    }
    
    public void avaluaComportament() {
        final boolean enemic = false;
        ++this.temps;
        this.estat = this.estatCombat();
        if (this.espera > 0) {
            --this.espera;
        }
        else {
            this.atura();
            this.camina();
            this.agafaCoses();
            this.bloquejat();
            this.ataca();
            this.defensa();
        }
    }
    
    void defensa() {
        if (this.estat.impactesRebuts > this.ferides || this.estat.llançamentEnemicDetectat) {
            this.ferides = this.estat.impactesRebuts;
            if (!this.estat.escutActivat && this.estat.escuts > 0) {
                this.activaEscut();
            }
            else if (!this.estat.veigAlgunEnemic) {
                this.gira(180);
            }
            else {
                this.gira(90);
            }
        }
    }
    
    void ataca() {
        if (this.estat.objecteVisor[1] == 1) {
            this.llança();
        }
    }
    
    void bloquejat() {
        if (this.estat.enCollisio) {
            ++this.tempsEnCollisio;
            if (this.tempsEnCollisio > 5) {
                this.hyperespai();
            }
            this.missatge("Estic en col.lisio");
            if (this.estat.objecteVisor[1] == 1) {
                this.llança();
            }
            else {
                if (this.hiHaParedDavant(20)) {
                    if (this.estat.objecteVisor[0] == 0 && this.estat.objecteVisor[2] == 0) {
                        if (this.estat.distanciaVisors[0] > this.estat.distanciaVisors[2]) {
                            this.esquerra();
                        }
                        else {
                            this.dreta();
                        }
                    }
                    else if (this.random.nextFloat() < 0.5) {
                        this.esquerra();
                    }
                    else {
                        this.dreta();
                    }
                    this.enrere();
                }
                else {
                    this.atura();
                    this.endavant();
                }
                this.espera = 8;
            }
        }
        else {
            this.tempsEnCollisio = 0;
        }
    }
    
    void camina() {
        int sensor = 0;
        double dEsq = 0.0;
        double dDre = 0.0;
        double dCen = 0.0;
        if (this.estat.objecteVisor[0] == 0 && this.estat.distanciaVisors[0] < 40.0) {
            sensor += 4;
            dEsq = this.estat.distanciaVisors[0];
        }
        if (this.estat.objecteVisor[1] == 0 && this.estat.distanciaVisors[1] < 40.0) {
            sensor += 2;
            dCen = this.estat.distanciaVisors[1];
        }
        if (this.estat.objecteVisor[2] == 0 && this.estat.distanciaVisors[2] < 40.0) {
            ++sensor;
            dDre = this.estat.distanciaVisors[2];
        }
        String bits = "";
        if ((sensor & 0x4) > 0) {
            bits += "1 ";
        }
        else {
            bits += "0 ";
        }
        if ((sensor & 0x2) > 0) {
            bits += "1 ";
        }
        else {
            bits += "0 ";
        }
        if ((sensor & 0x1) > 0) {
            bits += "1 ";
        }
        else {
            bits += "0 ";
        }
        this.missatge(bits);
        this.endavant();
        switch (sensor) {
            case 0: {
                if (this.estat.distanciaVisors[0] - this.estat.distanciaVisors[2] > 150.0) {
                    this.esquerra();
                    break;
                }
                if (this.estat.distanciaVisors[2] - this.estat.distanciaVisors[0] > 150.0) {
                    this.dreta();
                    break;
                }
                break;
            }
            case 1:
            case 3: {
                this.esquerra();
                this.espera = 3;
                break;
            }
            case 4:
            case 6: {
                this.dreta();
                this.espera = 3;
            }
            case 2:
            case 7: {
                if (dEsq < dDre) {
                    this.dreta();
                }
                else if (this.estat.distanciaVisors[0] > this.estat.distanciaVisors[2]) {
                    this.esquerra();
                }
                else if (this.random.nextFloat() < 0.5) {
                    this.esquerra();
                }
                else {
                    this.dreta();
                }
                this.espera = 10;
                break;
            }
        }
    }
    
    boolean hiHaParedDavant(final int dist) {
        return (this.estat.objecteVisor[0] == 0 && this.estat.distanciaVisors[0] <= dist) || (this.estat.objecteVisor[1] == 0 && this.estat.distanciaVisors[1] <= dist) || (this.estat.objecteVisor[2] == 0 && this.estat.distanciaVisors[2] <= dist);
    }
    
    void agafaCoses() {
        final Objecte objecte = this.objecteMesProper(this.estat.numObjectes, this.estat.objectes);
        if (objecte != null) {
            final int sector = objecte.agafaSector();
            switch (objecte.agafaTipus()) {
                case 1:
                case 3: {
                    if (sector == 2 || sector == 3) {
                        this.mira(objecte);
                        break;
                    }
                    if (sector == 1) {
                        this.dreta();
                        break;
                    }
                    this.esquerra();
                    break;
                }
                default: {
                    final int subtipus = objecte.agafaTipus() % 100;
                    this.missatge("sector " + sector + "   (" + this.estat.id + ":" + subtipus + ") ");
                    if (subtipus == this.estat.id) {
                        if (sector == 2 || sector == 3) {
                            this.mira(objecte);
                            break;
                        }
                        this.espera = 3;
                        if (sector == 1) {
                            this.dreta();
                            break;
                        }
                        this.esquerra();
                        break;
                    }
                    else {
                        if (sector == 1) {
                            break;
                        }
                        if (sector == 4) {
                            break;
                        }
                        if (sector == 2) {
                            this.esquerra();
                            break;
                        }
                        this.dreta();
                        break;
                    }
                    //break;
                }
            }
        }
    }
    
    Objecte objecteMesProper(final int num, final Objecte[] o) {
        int n = -1;
        int d = 9999;
        int nd = 0;
        for (int i = 0; i < num; ++i) {
            nd = o[i].agafaDistancia();
            if (nd < d) {
                n = i;
                d = nd;
            }
        }
        if (n != -1) {
            return o[n];
        }
        return null;
    }
}

