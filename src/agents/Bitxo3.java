package agents;

/*
    BITXO UNION
 */
import java.util.Random;

public class Bitxo3 extends Agent {

    static final int PARET = 0;
    static final int BITXO = 1;
    static final int RES = -1;

    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;

    private static final int MAX_DIST_BALES = 405; //RENAME de MAX_VISIO a MAX_DIST_BALES ya que este valor es la distancia máxima que puede recorrer una bala, no la vision

    private Estat estat;
    private Random random;
    private Accio accio;
    private int repetir, darrer_gir;
    private boolean mirant;

    public Bitxo3(Agents pare) {
        super(pare, "Nosotros <3", "imatges/bobEsponja.gif");
    }

    @Override
    public void inicia() {
        // atributsAgents(v,w,dv,av,ll,es,hy)
        int cost = atributsAgent(7, 8, 600, 45, 30, 0, 0);
        System.out.println("Cost total: " + cost);
        // Inicialització de variables que utilitzaré al meu comportament
        repetir = 0;
        accio = Accio.ENDAVANT;
        mirant = false;
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

    private void deteccioRecursos() {
        if (estat.veigAlgunRecurs) { //He quitado esto: estat.numObjectes > 0 && , ya que es redundante
            int distanciaMin = 9999; //He cambiado MAX_DIST_BALES por 9999 porque no tenia sentido
            int distanciaActual;

            int distanciaMinRecAliado = 99999;
            int distanciaMinRecEnemigo = MAX_DIST_BALES; //Aquí si que se puede usar

            Objecte objRecAliadoMasCercano = null;
            Objecte objRecEnemigoMasCercano = null;

            for (Objecte objActual : estat.objectes) { //Per a cada objecte
                if ((objActual != null) && (objActual.agafaSector() == 2 || objActual.agafaSector() == 3)) {
                    distanciaActual = objActual.agafaDistancia();
                    if (esRecursAliat(objActual)) {
                        if (distanciaActual < distanciaMinRecAliado) {
                            distanciaMinRecAliado = distanciaActual;
                            objRecAliadoMasCercano = objActual;
                        }
                    } else if (objActual.agafaTipus() >= 100 && !estat.llançant && !esRecursAliat(objActual)) {
                        if (distanciaActual < distanciaMinRecEnemigo) {
                            distanciaMinRecEnemigo = distanciaActual;
                            objRecEnemigoMasCercano = objActual;
                        }
                    }
                }
            }

            //Salgo de este for con el recurso/agente enemigo y aliado más cercano 
            if (distanciaMinRecEnemigo < MAX_DIST_BALES
                    && estat.llançaments > 0 && !estat.llançant
                    && objRecEnemigoMasCercano != null) {
                mira(objRecEnemigoMasCercano);
                System.out.println(objRecEnemigoMasCercano.agafaIndex());
                llança();
                mirant = true;
            }

            if (objRecAliadoMasCercano != null) {
                mira(objRecAliadoMasCercano);
                mirant = true;
            } else {
                mirant = false;
            }

        } else {
            mirant = false;
        }
    }

    private boolean esRecursAliat(Objecte obj) {
        return obj.agafaTipus() == (100 + estat.id);
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
            }
            gira(darrer_gir);
            comprobaMirant();
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

    private enum Accio {
        ESQUERRA,
        DRETA,
        VOLTEJ,
        ENDAVANT,
        DESFER
    }
}
