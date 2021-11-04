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
    private Objecte objPropersSecDosTres[], objPropers[];
    private int distMinSecDosTres[], distMin[];
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

    private void deteccioObjectes() {
        if (estat.veigAlgunRecurs || estat.veigAlgunEscut || estat.veigAlgunEnemic) {
            distMinSecDosTres[AGENT] = 300;
            distMinSecDosTres[RECURS_ALIAT] = distMinSecDosTres[ESCUT] = 9999;
            distMinSecDosTres[RECURS_ENEMIC] = MAX_DIST_BALES;

            distMin[AGENT] = 300;
            distMin[RECURS_ALIAT] = distMin[ESCUT] = 9999;
            distMin[RECURS_ENEMIC] = MAX_DIST_BALES;
            for (Objecte objActual : estat.objectes) {
                if (objActual != null) {
                    if (objActual.agafaTipus() == (100 + estat.id)) {
                        if (objActual.agafaDistancia() < distMin[RECURS_ALIAT]) {
                            distMin[RECURS_ALIAT] = objActual.agafaDistancia();
                            objPropers[RECURS_ALIAT] = objActual;
                        }
                    } else if (objActual.agafaTipus() == Estat.AGENT && !estat.llançant) {
                        if (objActual.agafaSector() == 2 || objActual.agafaSector() == 3) {
                            if (objActual.agafaDistancia() < distMinSecDosTres[AGENT]) {
                                objPropersSecDosTres[AGENT] = objActual;
                            }
                        } else {
                            if (objActual.agafaDistancia() < distMin[AGENT]) {
                                objPropers[AGENT] = objActual;
                            }
                        }
                    } else if ((objActual.agafaTipus() >= 100) && !estat.llançant) {
                        if (objActual.agafaSector() == 2 || objActual.agafaSector() == 3) {
                            if (objActual.agafaDistancia() < distMinSecDosTres[RECURS_ENEMIC]) {
                                distMinSecDosTres[RECURS_ENEMIC] = objActual.agafaDistancia();
                                objPropersSecDosTres[RECURS_ENEMIC] = objActual;
                            }
                        } else {
                            if (objActual.agafaDistancia() < distMin[RECURS_ENEMIC]) {
                                distMin[RECURS_ENEMIC] = objActual.agafaDistancia();
                                objPropers[RECURS_ENEMIC] = objActual;
                            }
                        }
                    } else if (objActual.agafaTipus() == Estat.ESCUT) {
                        if (objActual.agafaSector() == 2 || objActual.agafaSector() == 3) {
                            if (objActual.agafaDistancia() < distMinSecDosTres[ESCUT]) {
                                distMinSecDosTres[ESCUT] = objActual.agafaDistancia();
                                objPropersSecDosTres[ESCUT] = objActual;
                            }
                        } else {
                            if (objActual.agafaDistancia() < distMin[ESCUT]) {
                                distMin[ESCUT] = objActual.agafaDistancia();
                                objPropers[ESCUT] = objActual;
                            }
                        }
                    }
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
