package agents;

/*
    BITXO UNION
 */
import java.util.Random;

public class Bitxo1 extends Agent {

    static final int PARET = 0;
    static final int BITXO = 1;
    static final int RES = -1;

    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;

    private static final int AGENT = 0;
    private static final int RECURS_ALIAT = 1;
    private static final int RECURS_ENEMIC = 2;
    private static final int ESCUT = 3;

    private static final int MAX_DIST_BALES = 400;

    private Estat estat;
    private Random random;
    private Accio accio;
    private int repetir, darrer_gir;
    private Objecte objPropers[], objPropersSecDosTres[];
    private int distMin[], distMinSecDosTres[];
    private static int angle = 60;
    private boolean llançant;
    private int impactes;

    public Bitxo1(Agents pare) {
        super(pare, "3", "imatges/robotank3.gif");
    }

    @Override
    public void inicia() {
        // atributsAgents(v,w,dv,av,ll,es,hy)
        int cost = atributsAgent(5, 0, 600, angle, 30, 5, 0);
        System.out.println("Cost total: " + cost);
        // Inicialització de variables que utilitzaré al meu comportament
        repetir = 0;
        impactes = 0;
        llançant = false;
        accio = Accio.ENDAVANT;
        random = new Random();
        objPropersSecDosTres = new Objecte[4];
        objPropers = new Objecte[4];
        distMinSecDosTres = new int[4];
        distMin = new int[4];
    }

    @Override
    public void avaluaComportament() {
        estat = estatCombat();
        if (estat.indexNau[CENTRAL] != (100 + estat.id) && llançant) {
            llança();
            llançant = false;
        } else if (!repetirAccio()) {
            deteccioObjectes();
            deteccioDispar();
            deteccioParet();
        }

    }

    private void deteccioParet() {
        if (estat.enCollisio) {
            if (estat.objecteVisor[CENTRAL] == BITXO) {
                llança();
            } else {
                accio = Accio.VOLTEJ;
                repetir = 1;
            }
        } else if (estat.distanciaVisors[CENTRAL] < 65
                && estat.objecteVisor[CENTRAL] == PARET) {
            if (random.nextBoolean()) {
                accio = Accio.DRETA;
            } else {
                accio = Accio.ESQUERRA;
            }
            repetir = 3;
        } else if (estat.distanciaVisors[ESQUERRA] < 35
                && estat.objecteVisor[ESQUERRA] == PARET) {
            if ((estat.distanciaVisors[ESQUERRA] <= estat.distanciaVisors[DRETA]) && estat.objecteVisor[DRETA] == PARET) {
                accio = Accio.DRETA;
                repetir = 3;
            } else if ((estat.distanciaVisors[ESQUERRA] < estat.distanciaVisors[DRETA]) && estat.objecteVisor[DRETA] == PARET) {
                accio = Accio.ESQUERRA;
                repetir = 3;
            }
            accio = Accio.DRETA;
            repetir = 3;
        } else if (estat.distanciaVisors[DRETA] < 35
                && estat.objecteVisor[DRETA] == PARET) {
            if ((estat.distanciaVisors[DRETA] <= estat.distanciaVisors[ESQUERRA]) && estat.objecteVisor[ESQUERRA] == PARET) {
                accio = Accio.ESQUERRA;
                repetir = 3;
            } else if ((estat.distanciaVisors[DRETA] < estat.distanciaVisors[ESQUERRA]) && estat.objecteVisor[ESQUERRA] == PARET) {
                accio = Accio.DRETA;
                repetir = 3;
            }
            accio = Accio.ESQUERRA;
            repetir = 3;
        } else {
            endavant();
        }
    }

    private void initObjectes() {
        for (int i = 0; i < objPropers.length; i++) {
            objPropers[i] = null;
            objPropersSecDosTres[i] = null;
        }

        distMinSecDosTres[AGENT] = 300;
        distMinSecDosTres[RECURS_ALIAT] = distMinSecDosTres[ESCUT] = 9999;
        distMinSecDosTres[RECURS_ENEMIC] = MAX_DIST_BALES;

        distMin[AGENT] = 300;
        distMin[RECURS_ALIAT] = distMin[ESCUT] = 9999;
        distMin[RECURS_ENEMIC] = MAX_DIST_BALES;
    }

    private void objDistMin(Objecte obj) {
        int tipus = getTipusObj(obj);
        if (tipus != -1) {
            if((obj.agafaSector() == 2 || obj.agafaSector() == 3)
                    && obj.agafaDistancia() < distMinSecDosTres[tipus]){
                distMinSecDosTres[tipus] = obj.agafaDistancia();
                objPropersSecDosTres[tipus] = obj;
            }
            if(obj.agafaDistancia() < distMin[tipus]){
                distMin[tipus] = obj.agafaDistancia();
                objPropers[tipus] = obj;
            }
        }
    }

    private int getTipusObj(Objecte obj) {
        int tipus = -1;
        int tipusObjecte = obj.agafaDistancia();
        if (tipusObjecte == (100 + estat.id)) {
            tipus = RECURS_ALIAT;
        } else if (tipusObjecte == Estat.AGENT && !estat.llançant) {
            tipus = AGENT;
        } else if (tipusObjecte >= 100 && !estat.llançant) {
            tipus = RECURS_ENEMIC;
        } else if (tipusObjecte == Estat.ESCUT) {
            tipus = ESCUT;
        }
        return tipus;
    }

    private void deteccioObjectes() {
        if (estat.veigAlgunRecurs || estat.veigAlgunEscut || estat.veigAlgunEnemic) {
            initObjectes();
            for (Objecte objActual : estat.objectes) {
                if (objActual != null) {
                    objDistMin(objActual);
                }
            }
            if (objPropers[RECURS_ALIAT] != null) {
                switch (objPropers[RECURS_ALIAT].agafaSector()) {
                    case 1:
                        gira(90 - angle);
                        break;
                    case 2:
                        mira(objPropers[RECURS_ALIAT]);
                        break;
                    case 3:
                        mira(objPropers[RECURS_ALIAT]);
                        break;
                    case 4:
                        gira(360 - (90 - angle));
                        break;
                }
            } else if (objPropers[ESCUT] != null && objPropersSecDosTres[ESCUT] != null) {
                if (distMin[ESCUT] < distMinSecDosTres[ESCUT]) {
                    if (objPropers[ESCUT].agafaSector() == 4) {
                        gira(360 - (90 - angle));
                    } else if (objPropers[ESCUT].agafaSector() == 1) {
                        gira(90 - angle);
                    }
                    mira(objPropers[ESCUT]);
                } else {
                    mira(objPropersSecDosTres[ESCUT]);
                }
            } else if (objPropers[ESCUT] != null && objPropersSecDosTres[ESCUT] == null) {
                if (objPropers[ESCUT].agafaSector() == 4) {
                    gira(360 - (90 - angle));
                } else if (objPropers[ESCUT].agafaSector() == 1) {
                    gira(90 - angle);
                }
                mira(objPropers[ESCUT]);
            } else if (objPropers[ESCUT] == null && objPropersSecDosTres[ESCUT] != null) {
                mira(objPropersSecDosTres[ESCUT]);
            }

            if (objPropers[AGENT] != null && distMin[AGENT] < 30) {
                if (objPropers[AGENT].agafaSector() == 4) {
                    gira(360 - (90 - angle));
                } else if (objPropers[AGENT].agafaSector() == 1) {
                    gira(90 - angle);
                }
                mira(objPropers[AGENT]);
                llançant = true;
            } else if (estat.llançaments > 0 && objPropersSecDosTres[AGENT] != null) {
                mira(objPropersSecDosTres[AGENT]);
                llançant = true;
            } else if (estat.llançaments > 0 && objPropersSecDosTres[RECURS_ENEMIC] != null) {
                mira(objPropersSecDosTres[RECURS_ENEMIC]);
                llançant = true;
            } else if (objPropers[RECURS_ENEMIC] != null) {
                if (objPropers[RECURS_ENEMIC].agafaSector() == 4) {
                    gira(360 - (90 - angle));
                } else if (objPropers[RECURS_ENEMIC].agafaSector() == 1) {
                    gira(90 - angle);
                }
                mira(objPropers[RECURS_ENEMIC]);
                llançant = true;
            }
        }
    }

    private boolean repetirAccio() {
        if (repetir != 0) {
            switch (accio) {
                case ESQUERRA:
                    darrer_gir = 10 + random.nextInt(10);
                    break;
                case DRETA:
                    darrer_gir = -(10 + random.nextInt(10));
                    break;
                case VOLTEJ:
                    darrer_gir = 180 + (random.nextInt(90) - 45); //Gira 135-225 grados
                    break;
            }
            gira(darrer_gir);
            repetir--;
            return true;
        } else {
            return false;
        }
    }

    private void deteccioDispar() {
        if (estat.llançamentEnemicDetectat && estat.escutActivat == false
                && estat.escuts > 0 && estat.distanciaLlançamentEnemic > 100) {
            activaEscut();
        } else if (estat.impactesRebuts > impactes && estat.escutActivat == false
                && estat.escuts > 0) {
            impactes = estat.impactesRebuts;
            activaEscut();
        }
    }

    private enum Accio {
        ESQUERRA,
        DRETA,
        VOLTEJ,
        ENDAVANT,
    }
}
