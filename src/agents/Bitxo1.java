package agents;

import java.util.Random;

/*
    BITXO NOSOTROS <3
 */
public class Bitxo1 extends Agent {

    static final int PARET = 0;
    static final int BITXO = 1;
    static final int RES = -1;

    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;

    private static final int MAX_VISIO = 405;

    private Estat estat;
    private Random random;
    private Accio accio;
    private int repetir, darrer_gir;
    private boolean mirant;

    public Bitxo1(Agents pare) {
        super(pare, "Nosotros <3", "imatges/robotank1.gif");
    }

    @Override
    public void inicia() {
        // atributsAgents(v,w,dv,av,ll,es,hy)
        int cost = atributsAgent(6, 8, 600, 30, 23, 5, 5);
        System.out.println("Cost total: " + cost);
        // Inicialització de variables que utilitzaré al meu comportament
        repetir = 0;
        accio = Accio.ENDAVANT;
        random = new Random();
    }

    @Override
    public void avaluaComportament() {
        if (!repetirAccio()) {
            estat = estatCombat();
            deteccioRecursos();
            deteccioParet();
        }
    }

    private enum Accio {
        ESQUERRA,
        DRETA,
        VOLTEJ,
        ENDAVANT,
        DESFER,
        CONTINUA
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
                    darrer_gir = 180 + (random.nextInt(90) - 45);
                    break;
                case DESFER:
                    darrer_gir = -(darrer_gir);
                    break;
                case CONTINUA:
                    break;
            }
            gira(darrer_gir);
            //comprobaMirant();
            repetir--;
            return true;
        } else {
            return false;
        }
    }

    private void comprobaMirant() {
        if (mirant) {
            accio = Accio.DESFER;
            repetir = 1;
        }
    }

    private void deteccioRecursos() {
        if (estat.numObjectes > 0 && estat.veigAlgunRecurs) {
            int distActualRecAli, distMinRecAli = Integer.MAX_VALUE,
                    distActualRecEne, distMinRecEne = MAX_VISIO;
            Objecte recAliMesProper, recEneMesProper;
            recAliMesProper = recEneMesProper = null;
            for (Objecte objActual : estat.objectes) {
                if (objActual != null) {
                    int tipusRec = objActual.agafaTipus();
                    if (esRecAliEsc(tipusRec)) {
                        distActualRecAli = objActual.agafaDistancia();
                        if (distActualRecAli < distMinRecAli) {
                            distMinRecAli = distActualRecAli;
                            recAliMesProper = objActual;
                        }
                    } else if (tipusRec > 100 && !esRecAliEsc(tipusRec)) {
                        distActualRecEne = objActual.agafaDistancia();
                        if (distActualRecEne < distMinRecEne) {
                            distMinRecEne = distActualRecEne;
                            recEneMesProper = objActual;
                        }
                    }
                }
            }
            if (recEneMesProper != null && estat.llançaments > 0 
                    && !estat.llançant && distMinRecEne < distMinRecAli) {
                mira(recEneMesProper);
                llança();
            }
            if (recAliMesProper != null) {
                mira(recAliMesProper);
            }
        }
    }

    private boolean esRecAliEsc(int tipus) {
        return tipus == (100 + estat.id) || tipus == Estat.ESCUT;
    }

    private void deteccioParet() {
        if (estat.enCollisio) {
            accio = Accio.VOLTEJ;
            repetir = 1;
        } else if (estat.distanciaVisors[CENTRAL] < 30
                && estat.objecteVisor[CENTRAL] == PARET) {
            if (random.nextBoolean()) {
                accio = Accio.DRETA;
            } else {
                accio = Accio.ESQUERRA;
            }
            repetir = 3;
        } else if (estat.distanciaVisors[ESQUERRA] < 40
                && estat.objecteVisor[ESQUERRA] == PARET) {
            accio = Accio.DRETA;
            repetir = 3;
        } else if (estat.distanciaVisors[DRETA] < 40
                && estat.objecteVisor[DRETA] == PARET) {
            accio = Accio.ESQUERRA;
            repetir = 3;
        } else {
            endavant();
        }
    }
}
