package agents;

<<<<<<< Updated upstream
import java.lang.Math;

// Exemple de Bitxo
=======
/*
    BITXO UNION
 */
import java.util.Random;

>>>>>>> Stashed changes
public class Bitxo2 extends Agent {

    static final int PARET = 0;
    static final int BITXO = 1;
    static final int RES = -1;

    static final int ESQUERRA = 0;
<<<<<<< Updated upstream
    static final int ENDAVANT = 1;
    static final int CENTRAL = 1;
    static final int DRETA = 2;
    static final int ENRERE = 3;
    static final int DONAVOLTA = 4;

    static final int ALERTA = 0;
    static final int A = 1;
    static final int B = 2;

    int repetir;
    int accio;
    int estatBitxo;
    int objI;
    boolean EsticMirant;
    //boolean heMirat;
    Estat estat;
    Objecte[] obj;

    //Esto nos permite encontrar el recurso nuestro mas cercano
    Objecte recursPropiLocal;
    Objecte recursPropiGlobal;
    Objecte recursEnemicLocal;
    Objecte recursEnemicGlobal;
    int dist_recurs_Propia_Global;
    int dist_recurs_Propia_Local;
    int dist_recurs_Enemic_Global;
    int dist_recurs_Enemic_Local;
    boolean heTrobatPropi;
    boolean heTrobatEnemic;

    int sectorLocal;
    int sectorGlobal;

    int recursosActuals;

    int graus;
    int girs;

    int polla = 0;

    public Bitxo2(Agents pare) {
        super(pare, "Bitxo1", "imatges/robotank1.gif");
=======
    static final int CENTRAL = 1;
    static final int DRETA = 2;

    private static final int MAX_DIST_BALES = 405; //RENAME de MAX_VISIO a MAX_DIST_BALES ya que este valor es la distancia máxima que puede recorrer una bala, no la vision

    private Estat estat;
    private Random random;
    private Accio accio;
    private int repetir, darrer_gir;
    private boolean mirant;

    public Bitxo2(Agents pare) {
        super(pare, "MALO2", "imatges/bobEsponjaMalo.gif");
>>>>>>> Stashed changes
    }

    @Override
    public void inicia() {
<<<<<<< Updated upstream

        // atributsAgents(v,w,dv,av,ll,es,hy)
        graus = 45;
        int cost = atributsAgent(7, 9, 600, graus, 40, 5, 0);
        System.out.println("Cost total:" + cost);

        // Inicialització de variables que utilitzaré al meu comportament   
        repetir = 0;
        objI = 0;
        estatBitxo = ALERTA;
        EsticMirant = false;
        heTrobatPropi = false;
        // heMirat = false;
        sectorLocal = 0;
        sectorGlobal = 0;
        recursosActuals = 0;

        dist_recurs_Propia_Global = 9999;

        girs = 1;//(int) Math.ceil(360 / graus);
=======
        // atributsAgents(v,w,dv,av,ll,es,hy)
        int cost = atributsAgent(5, 6, 600, 45, 30, 0, 3);
        System.out.println("Cost total: " + cost);
        // Inicialització de variables que utilitzaré al meu comportament
        repetir = 0;
        accio = Accio.ENDAVANT;
        mirant = false;
        random = new Random();

>>>>>>> Stashed changes
    }

    @Override
    public void avaluaComportament() {
<<<<<<< Updated upstream
        estat = estatCombat();
        if (repetir != 0) {
            repetirAccio();
            repetir--;
        } else {
            
            if (estat.enCollisio) {
                atura();
                accio = ENRERE;
                repetir = 5;
            } else {

                cerca();
                if (heTrobatEnemic && estat.llançaments > 0) {
                    mira(recursEnemicGlobal);
                    llança();
                } else if (heTrobatEnemic) {
                    mira(recursPropiGlobal);
                }

                if (estat.distanciaVisors[ESQUERRA] < 20 && (estat.objecteVisor[ESQUERRA] == PARET)) {
                    accio = DRETA;
                    repetir = 1;
                } else if (estat.distanciaVisors[DRETA] < 20 && (estat.objecteVisor[DRETA] == PARET)) {
                    accio = ESQUERRA;
                    repetir = 1;
                }
                endavant();
            }
        }
    }

    private void repetirAccio() {
        switch (accio) {
            case 0:
                //esquerra();
                gira(30);
                break;
            case 1:
                endavant();
                break;
            case 2:
                //dreta();
                gira(-30);
                break;
            case 3:
                enrere();
                if (repetir == 1) {
                    gira(30);
                }
                break;
            case 4:
                gira(graus * 2);
                cerca();
                sectorLocal++;
                break;
        }
    }

    private void cerca() {
        obj = estat.objectes;
        heTrobatPropi = false;
        heTrobatEnemic = false;
        recursPropiLocal = null;
        recursEnemicLocal = null;
        dist_recurs_Propia_Global = 9999;
        dist_recurs_Enemic_Global = 9999;
        for (Objecte o : obj) {
            if (o != null && o.agafaTipus() == (101)) {
                if (o.agafaDistancia() < dist_recurs_Propia_Global) {
                    dist_recurs_Propia_Global = o.agafaDistancia();
                    recursPropiGlobal = o;
                    heTrobatPropi = true;
                }
            } else if (o != null && (o.agafaTipus() >= 100) && (o.agafaTipus() != (101))) {
                if (o.agafaDistancia() < dist_recurs_Enemic_Global) {
                    dist_recurs_Enemic_Global = o.agafaDistancia();
                    recursEnemicGlobal = o;
                    heTrobatPropi = true;
                }
            }
        }
    }
=======
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
>>>>>>> Stashed changes
}
