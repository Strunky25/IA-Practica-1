package agents;

/*
    BITXO UNION
 */
import java.util.Random;

public class Bitxo2 extends Agent {

    static final int PARET = 0;
    static final int BITXO = 1;
    static final int RES = -1;

    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;

    private static final int MAX_DIST_BALES = 400;

    private static final int ANGLE = 45;
    private static final int SECTOR1 = -(90 - ANGLE);
    private static final int SECTOR4 = -SECTOR1;

    private static final int AGENT_ENEMIC = 0;
    private static final int RECURS_ALIAT = 1;
    private static final int RECURS_ENEMIC = 2;
    private static final int ESCUT = 3;

    private Objecte objPropers[], objPropersSecDosTres[];
    private int distMin[], distMinSecDosTres[];
    private int repetir, darrer_gir, impactes, tipusLocalitzat, temps;
    private boolean llançant;
    private Estat estat;
    private Random random;
    private Accio accio;

    public Bitxo2(Agents pare) {
        super(pare, "Bitxo 2", "imatges/robotank1.gif");
    }

    @Override
    public void inicia() {
        // atributsAgents(v,w,dv,av,ll,es,hy)
        int cost = atributsAgent(6, 4, 699, ANGLE, 56, 5, 0);
        System.out.println("Cost total: " + cost);

        repetir = impactes = darrer_gir = temps = 1;
        llançant = false;
        tipusLocalitzat = RES;
        accio = Accio.ENDAVANT;
        random = new Random();
        objPropersSecDosTres = objPropers = new Objecte[4];
        distMinSecDosTres = distMin = new int[4];
    }

    @Override
    public void avaluaComportament() {
        //System.out.println(temps);
        estat = estatCombat();
        comprobarLlançament();
        if (tipusLocalitzat != RES) {
            comprobarLocalitzat();
        } else if (!repetirAccio()) {
            deteccioObjectes();
            persegueix();
            deteccioDisparEnemic();
            deteccioParet();
        }
        temps--;
    }

    private void comprobarLlançament() {
        if (estat.indexNau[CENTRAL] != (100 + estat.id) && llançant && temps > 0) {
            llança();
            llançant = false;
        }
    }

    private void comprobarLocalitzat() {
        if (tipusLocalitzat == AGENT_ENEMIC || tipusLocalitzat == RECURS_ENEMIC) {
            llançant = true;
            temps = 5;
        }
        mira(objPropers[tipusLocalitzat]);
        tipusLocalitzat = RES;
    }

    private void deteccioObjectes() {
        if (estat.veigAlgunRecurs || estat.veigAlgunEscut || estat.veigAlgunEnemic) {
            initArrayObjectes();
            cercaObjectesMesPropers();

            if (objPropers[RECURS_ALIAT] != null) {
                cercaTipusObjecte(RECURS_ALIAT);
                tipusLocalitzat = RECURS_ALIAT;
            } else if (objPropersSecDosTres[ESCUT] != null) {
                mira(objPropersSecDosTres[ESCUT]);
            }
            if (objPropers[AGENT_ENEMIC] != null && objPropers[AGENT_ENEMIC].agafaDistancia() < 250) {
                disparaObjecteEnemic(AGENT_ENEMIC);
            } else if (objPropers[RECURS_ENEMIC] != null && objPropers[RECURS_ENEMIC].agafaDistancia() < 400) {
                disparaObjecteEnemic(RECURS_ENEMIC);
            }
        }
    }

    private void persegueix() {
        if (estat.llançaments > 0) {
            if (objPropers[RECURS_ALIAT] == null && objPropers[ESCUT] == null
                    && objPropersSecDosTres[RECURS_ALIAT] == null && objPropersSecDosTres[ESCUT] == null
                    && objPropers[AGENT_ENEMIC] != null) {
                cercaTipusObjecte(AGENT_ENEMIC);
            } else if (objPropers[RECURS_ALIAT] == null && objPropers[ESCUT] == null
                    && objPropersSecDosTres[RECURS_ALIAT] == null && objPropersSecDosTres[ESCUT] == null
                    && (objPropers[RECURS_ENEMIC] != null || objPropers[RECURS_ENEMIC] != null)) {
                cercaTipusObjecte(RECURS_ENEMIC);
            }
        }
    }

    private void deteccioDisparEnemic() {
        //Si detect dispar enemic a menys de 100px,  li queden escuts i no en té cap d'actiu -> Activa escut 
        if (estat.llançamentEnemicDetectat && estat.escutActivat == false
                && estat.escuts > 0 && estat.distanciaLlançamentEnemic < 100) {
            activaEscut();
        } //Si ha rebut més impactes que a l'anterior iteració, li queden escuts i no en té cap d'actiu -> Activa escut 
        else if (estat.impactesRebuts > impactes && estat.escutActivat == false
                && estat.escuts > 0) {
            impactes = estat.impactesRebuts;
            activaEscut();
        }
    }

    private void deteccioParet() {
        if (estat.enCollisio) {
            //Si estic colisionant amb l'enemic per davant i me queden bales dispar
            if (estat.objecteVisor[CENTRAL] == BITXO && estat.llançaments > 0 && temps > 0 && estat.distanciaVisors[CENTRAL] < 10) {
                llança();
                temps = 10;
                if (estat.escutActivat == false && estat.escuts > 0) {
                    activaEscut();
                }
            } else if (estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[DRETA] < 25) {
                // enrere();
                accio = Accio.ESQUERRA;
                repetir = 2;
            } else if (estat.objecteVisor[ESQUERRA] == PARET && estat.distanciaVisors[ESQUERRA] < 25) {
                // enrere();
                accio = Accio.DRETA;
                repetir = 2;
            } else {
                //   enrere();
                accio = Accio.VOLTEJ;
                repetir = 1;
            }
        } else if (estat.distanciaVisors[CENTRAL] < 50 && estat.objecteVisor[CENTRAL] == PARET) {
            if (estat.objecteVisor[ESQUERRA] == PARET && estat.distanciaVisors[ESQUERRA] < estat.distanciaVisors[DRETA]) { //estat.distanciaVisors[ESQUERRA] < estat.distanciaVisors[CENTRAL] &&
                accio = Accio.DRETA;
                repetir = 2;
            } else if (estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[DRETA] < estat.distanciaVisors[ESQUERRA]) { //estat.distanciaVisors[DRETA] < estat.distanciaVisors[CENTRAL] &&
                accio = Accio.ESQUERRA;
                repetir = 2;
            }
        } else if (estat.distanciaVisors[CENTRAL] > 50 && estat.distanciaVisors[CENTRAL] < 150 && estat.objecteVisor[CENTRAL] == PARET) {//por si el visor central está a mas de 50 pero yo me estoy acercando a la pared
            if (estat.objecteVisor[ESQUERRA] == PARET && estat.distanciaVisors[ESQUERRA] < estat.distanciaVisors[DRETA]
                    && estat.distanciaVisors[ESQUERRA] < 25) {
                accio = Accio.DRETA;
                repetir = 2;
            } else if (estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[DRETA] < estat.distanciaVisors[ESQUERRA]
                    && estat.distanciaVisors[DRETA] < 25) {
                accio = Accio.ESQUERRA;
                repetir = 2;
            }
        } else {
            endavant();
        }
    }

    private void initArrayObjectes() {
        llançant = false;
        for (int i = 0; i < objPropers.length; i++) {
            objPropers[i] = null;
            objPropersSecDosTres[i] = null;
            distMin[i] = distMinSecDosTres[i] = 9999;
        }
    }

    private void cercaObjectesMesPropers() {
        for (Objecte obj : estat.objectes) {
            if (obj != null) {
                int tipus = getTipusObjecte(obj);
                if (tipus != RES) {
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
        return RES;
    }

    private void cercaTipusObjecte(int tipusObjecte) {
        switch (objPropers[tipusObjecte].agafaSector()) {
            case 1:
                gira(SECTOR1);
                break;
            case 4:
                gira(SECTOR4);
                break;
            default:
                mira(objPropers[tipusObjecte]);
                break;
        }
    }

    private void disparaObjecteEnemic(int tipusObj) {
        if (objPropersSecDosTres[tipusObj] != null && estat.llançaments > 0) {
            mira(objPropersSecDosTres[tipusObj]);
            llançant = true;
            temps = 10;
        } else if (tipusLocalitzat == RES && estat.llançaments > 0) {
            if (objPropers[tipusObj].agafaSector() == 1) {
                gira(SECTOR1);
            } else if (objPropers[tipusObj].agafaSector() == 4) {
                gira(SECTOR4);
            }
            tipusLocalitzat = tipusObj;
        }
    }

    private boolean repetirAccio() {
        if (repetir != 0) {
            //atura();
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

    private enum Accio {
        ESQUERRA,
        DRETA,
        VOLTEJ,
        ENDAVANT,
    }
}
