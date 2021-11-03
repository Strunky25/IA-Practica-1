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

    private static final int MAX_DIST_BALES = 400; //RENAME de MAX_VISIO a MAX_DIST_BALES ya que este valor es la distancia máxima que puede recorrer una bala, no la vision

    private Estat estat;
    private Random random;
    private Accio accio;
    private int repetir, darrer_gir;

    private static int angle = 60;
    boolean llançant;
    int impactes;

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

    }

    @Override
    public void avaluaComportament() {
        estat = estatCombat();
        if (estat.indexNau[CENTRAL] != (100 + estat.id) && llançant) {
            llança();
            llançant = false;
        } else if (!repetirAccio()) {
            deteccioRecursos();
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

    private void deteccioRecursos() {
        if (estat.veigAlgunRecurs) { //He quitado esto: estat.numObjectes > 0 && , ya que es redundante
            Objecte recursosPropers_2y3[] = new Objecte[4];
            Objecte recursosPropers[] = new Objecte[4];

            int distanciesMinimes_2y3[] = new int[4];
            int distanciesMinimes[] = new int[4];

            int Agent = 0;
            int RecursAliat = 1;
            int RecursEnemic = 2;
            int Escut = 3;

            distanciesMinimes_2y3[Agent] = 300;
            distanciesMinimes_2y3[RecursAliat] = 9999;
            distanciesMinimes_2y3[RecursEnemic] = 400;
            distanciesMinimes_2y3[Escut] = 9999;

            distanciesMinimes[Agent] = 300;
            distanciesMinimes[RecursAliat] = 9999;
            distanciesMinimes[RecursEnemic] = 400;
            distanciesMinimes[Escut] = 9999;

            for (Objecte objActual : estat.objectes) { //Per a cada objecte
                if (objActual != null) {

                    if (objActual.agafaTipus() == (100 + estat.id)) {
                        if (objActual.agafaDistancia() < distanciesMinimes[RecursAliat]) {
                            distanciesMinimes[RecursAliat] = objActual.agafaDistancia();
                            recursosPropers[RecursAliat] = objActual;
                        }
                    } else if (objActual.agafaTipus() == Estat.AGENT && !estat.llançant) {
                        if (objActual.agafaSector() == 2 || objActual.agafaSector() == 3) {
                            if (objActual.agafaDistancia() < distanciesMinimes_2y3[Agent]) {
                                recursosPropers_2y3[Agent] = objActual;
                            }
                        } else {
                            if (objActual.agafaDistancia() < distanciesMinimes[Agent]) {
                                recursosPropers[Agent] = objActual;
                            }
                        }
                    } else if ((objActual.agafaTipus() >= 100) && !estat.llançant) {
                        if (objActual.agafaSector() == 2 || objActual.agafaSector() == 3) {
                            if (objActual.agafaDistancia() < distanciesMinimes_2y3[RecursEnemic]) {
                                distanciesMinimes_2y3[RecursEnemic] = objActual.agafaDistancia();
                                recursosPropers_2y3[RecursEnemic] = objActual;
                            }
                        } else {
                            if (objActual.agafaDistancia() < distanciesMinimes[RecursEnemic]) {
                                distanciesMinimes[RecursEnemic] = objActual.agafaDistancia();
                                recursosPropers[RecursEnemic] = objActual;
                            }
                        }
                    } else if (objActual.agafaTipus() == Estat.ESCUT) {
                        if (objActual.agafaSector() == 2 || objActual.agafaSector() == 3) {
                            if (objActual.agafaDistancia() < distanciesMinimes_2y3[Escut]) {
                                distanciesMinimes_2y3[Escut] = objActual.agafaDistancia();
                                recursosPropers_2y3[Escut] = objActual;
                            }
                        } else {
                            if (objActual.agafaDistancia() < distanciesMinimes[Escut]) {
                                distanciesMinimes[Escut] = objActual.agafaDistancia();
                                recursosPropers[Escut] = objActual;
                            }
                        }
                    }

                    if (recursosPropers[RecursAliat] != null) {
                        switch (recursosPropers[RecursAliat].agafaSector()) {
                            case 1:
                                gira(90 - angle);
                                break;
                            case 2:
                                mira(recursosPropers[RecursAliat]);
                                break;
                            case 3:
                                mira(recursosPropers[RecursAliat]);
                                break;
                            case 4:
                                gira(360 - (90 - angle));
                                break;
                        }
                    } else if (recursosPropers[Escut] != null && recursosPropers_2y3[Escut] != null) {
                        if (distanciesMinimes[Escut] < distanciesMinimes_2y3[Escut]) {
                            if (recursosPropers[Escut].agafaSector() == 4) {
                                gira(360 - (90 - angle));
                            } else if (recursosPropers[Escut].agafaSector() == 1) {
                                gira(90 - angle);
                            }
                            mira(recursosPropers[Escut]);
                        } else {
                            mira(recursosPropers_2y3[Escut]);
                        }
                    } else if (recursosPropers[Escut] != null && recursosPropers_2y3[Escut] == null) {
                        if (recursosPropers[Escut].agafaSector() == 4) {
                            gira(360 - (90 - angle));
                        } else if (recursosPropers[Escut].agafaSector() == 1) {
                            gira(90 - angle);
                        }
                        mira(recursosPropers[Escut]);
                    } else if (recursosPropers[Escut] == null && recursosPropers_2y3[Escut] != null) {
                        mira(recursosPropers_2y3[Escut]);
                    }

                    if (recursosPropers[Agent] != null && distanciesMinimes[Agent] < 30) {
                        if (recursosPropers[Agent].agafaSector() == 4) {
                            gira(360 - (90 - angle));
                        } else if (recursosPropers[Agent].agafaSector() == 1) {
                            gira(90 - angle);
                        }
                        mira(recursosPropers[Agent]);
                        llançant = true;
                    } else if (estat.llançaments > 0 && recursosPropers_2y3[Agent] != null) {
                        mira(recursosPropers_2y3[Agent]);
                        llançant = true;
                    } else if (estat.llançaments > 0 && recursosPropers_2y3[RecursEnemic] != null) {
                        mira(recursosPropers_2y3[RecursEnemic]);
                        llançant = true;
                    } else if (recursosPropers[RecursEnemic] != null) {
                        if (recursosPropers[RecursEnemic].agafaSector() == 4) {
                            gira(360 - (90 - angle));
                        } else if (recursosPropers[RecursEnemic].agafaSector() == 1) {
                            gira(90 - angle);
                        }
                        mira(recursosPropers[RecursEnemic]);
                        llançant = true;
                    }
                }
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
