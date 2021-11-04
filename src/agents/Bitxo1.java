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

    private static final int MAX_DIST_BALES = 400;

    private static final int ANGLE = 60;
    private static final int SECTOR1 = -(90 - ANGLE);
    private static final int SECTOR4 = -SECTOR1;

    private static final int AGENT_ENEMIC = 0;
    private static final int RECURS_ALIAT = 1;
    private static final int RECURS_ENEMIC = 2;
    private static final int ESCUT = 3;
    private static final int DESCONEGUT = -1;

    private Objecte objPropers[], objPropersSecDosTres[];
    private int distMin[], distMinSecDosTres[];
    private int repetir, darrer_gir, impactes;
    private boolean llançant, recLocalitzat, recEneLocalitzat, eneLocalitzat;
    private Estat estat;
    private Random random;
    private Accio accio;

    public Bitxo1(Agents pare) {
        super(pare, "3", "imatges/robotank3.gif");
    }

    @Override
    public void inicia() {
        // atributsAgents(v,w,dv,av,ll,es,hy)
        int cost = atributsAgent(5, 0, 600, ANGLE, 30, 5, 0);
        System.out.println("Cost total: " + cost);
        // Inicialització de variables que utilitzaré al meu comportament
        repetir = impactes = 0;
        llançant = eneLocalitzat = recEneLocalitzat = recLocalitzat = false;
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
        if (recLocalitzat && objPropers[RECURS_ALIAT] != null) {
            mira(objPropers[RECURS_ALIAT]);
            recLocalitzat = false;
        } else if (estat.indexNau[CENTRAL] != (100 + estat.id) && llançant) {
            llança();
            llançant = false;
        } else if (eneLocalitzat && objPropers[AGENT_ENEMIC] != null) {
            mira(objPropers[AGENT_ENEMIC]);
            eneLocalitzat = false;
            llançant = true;
        } else if (recEneLocalitzat && objPropers[RECURS_ENEMIC] != null) {
            mira(objPropers[RECURS_ENEMIC]);
            recEneLocalitzat = false;
            llançant = true;
        } else if (!repetirAccio()) {
            deteccioObjectes();
            deteccioDisparEnemic();
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

    private void initArrayObjectes() {
        llançant = recLocalitzat = false;
        for (int i = 0; i < objPropers.length; i++) {
            objPropers[i] = null;
            objPropersSecDosTres[i] = null;
        }

        distMinSecDosTres[AGENT_ENEMIC] = 250;
        distMinSecDosTres[RECURS_ALIAT] = distMinSecDosTres[ESCUT] = 9999;
        distMinSecDosTres[RECURS_ENEMIC] = MAX_DIST_BALES;

        distMin[AGENT_ENEMIC] = 250;
        distMin[RECURS_ALIAT] = distMin[ESCUT] = 9999;
        distMin[RECURS_ENEMIC] = MAX_DIST_BALES;
    }

    private void objectesDistanciaMinima() {
        for (Objecte obj : estat.objectes) {
            if (obj != null) {
                int tipus = getTipusObjecte(obj);
                if (tipus != DESCONEGUT) {
                    int distObj = obj.agafaDistancia();
                    if ((obj.agafaSector() == 2 || obj.agafaSector() == 3)
                            && distObj < distMinSecDosTres[tipus]) {
                        distMinSecDosTres[tipus] = distObj;
                        objPropersSecDosTres[tipus] = obj;
                    }
                    if (distObj < distMin[tipus]) {
                        distMin[tipus] = distObj;
                        objPropers[tipus] = obj;
                    }
                }
            }
        }
    }

    private int getTipusObjecte(Objecte obj) {
        int tipusObjecte = obj.agafaTipus();
        if (tipusObjecte == (100 + estat.id)) {
            return RECURS_ALIAT;
        } else if (tipusObjecte == Estat.AGENT && !estat.llançant) {
            return AGENT_ENEMIC;
        } else if (tipusObjecte >= 100 && !estat.llançant) {
            return RECURS_ENEMIC;
        } else if (tipusObjecte == Estat.ESCUT) {
            return ESCUT;
        }
        return DESCONEGUT;
    }

    private void cercaRecursAliat() {
        switch (objPropers[RECURS_ALIAT].agafaSector()) {
            case 1:
                gira(SECTOR1);
                recLocalitzat = true;
                break;
            case 4:
                gira(SECTOR4);
                recLocalitzat = true;
                break;
            default:
                mira(objPropers[RECURS_ALIAT]);
                break;
        }
    }

    private void disparaObjecteEnemic(int tipusObj) {
        if (objPropersSecDosTres[tipusObj] != null && estat.llançaments > 0) {
            mira(objPropersSecDosTres[tipusObj]);
            llançant = true;
        } else if (objPropers[RECURS_ALIAT] == null) {
            if (objPropers[tipusObj].agafaSector() == 1) {
                gira(SECTOR1);
            } else if (objPropers[tipusObj].agafaSector() == 4) {
                gira(SECTOR4);
            }
            if (tipusObj == AGENT_ENEMIC) {
                eneLocalitzat = true;
            } else if (tipusObj == RECURS_ENEMIC) {
                recEneLocalitzat = true; // CAMBIAR LOCALITZAT A TIPUS
            }
        }
    }

    private void deteccioObjectes() {
        if (estat.veigAlgunRecurs || estat.veigAlgunEscut || estat.veigAlgunEnemic) {
            initArrayObjectes();
            objectesDistanciaMinima();
            if (objPropers[RECURS_ALIAT] != null) {
                cercaRecursAliat();
            } else if (objPropers[AGENT_ENEMIC] != null) {
                disparaObjecteEnemic(AGENT_ENEMIC);
            } else if (objPropers[RECURS_ENEMIC] != null) {
                disparaObjecteEnemic(RECURS_ENEMIC);
            } else if (objPropersSecDosTres[ESCUT] != null) {
                mira(objPropersSecDosTres[ESCUT]);
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

    private void deteccioDisparEnemic() {
        if (estat.llançamentEnemicDetectat && estat.escutActivat == false
                && estat.escuts > 0 && estat.distanciaLlançamentEnemic < 100) { //< 100??
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
