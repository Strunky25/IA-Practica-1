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

    private static final int MAX_DIST_BALES = 400; //RENAME de MAX_VISIO a MAX_DIST_BALES ya que este valor es la distancia máxima que puede recorrer una bala, no la vision

    private Estat estat;
    private Random random;
    private Accio accio;
    private int repetir, darrer_gir;

    public Bitxo2(Agents pare) {
        super(pare, "Lift", "imatges/bobEsponjaMalo.gif");
    }

    @Override
    public void inicia() {
        // atributsAgents(v,w,dv,av,ll,es,hy)
        int cost = atributsAgent(5, 0, 600, 60, 30, 0, 0);
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
            deteccioDispar();
            deteccioParet();
        }

    }

    private void deteccioParet() {
        if (estat.enCollisio) {
            accio = Accio.VOLTEJ;
            repetir = 1;
        } else if (estat.distanciaVisors[CENTRAL] < 65
                && estat.objecteVisor[CENTRAL] == PARET) {
            if (random.nextBoolean()) {
                accio = Accio.DRETA;
            } else {
                accio = Accio.ESQUERRA;
            }
            repetir = 3;
        } else if (estat.distanciaVisors[ESQUERRA] < 45
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
        } else if (estat.distanciaVisors[DRETA] < 45
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
            int distanciaMin = 9999; //He cambiado MAX_DIST_BALES por 9999 porque no tenia sentido

            int distanciaActualAliado;
            int distanciaActualRecursoEnemigo;
            int distanciaActualEnemigo;

            int distanciaMinRecAliado = 99999;
            int distanciaMinRecEnemigo = MAX_DIST_BALES; //Aquí si que se puede usar
            int distanciaMinEnemigo = 300;

            Objecte objRecAliadoMasCercano = null;
            Objecte objRecEnemigoMasCercano = null;
            Objecte objEnemigoMasCercano = null;

            boolean dos_i_tres_buit = true;
            for (Objecte objActual : estat.objectes) { //Per a cada objecte
                if ((objActual != null) && (objActual.agafaSector() == 2 || objActual.agafaSector() == 3)) {
                    dos_i_tres_buit = false;
                    if (esRecursAliat(objActual)) {
                        distanciaActualAliado = objActual.agafaDistancia();
                        if (distanciaActualAliado < distanciaMinRecAliado) {
                            distanciaMinRecAliado = distanciaActualAliado;
                            objRecAliadoMasCercano = objActual;
                        }
                    } else if (objActual.agafaTipus() == Estat.AGENT && !estat.llançant) {
                        if (objActual.agafaDistancia() < distanciaMinEnemigo) {
                            objEnemigoMasCercano = objActual;
                        }
                    } else if ((objActual.agafaTipus() >= 100) && !estat.llançant) {
                        distanciaActualRecursoEnemigo = objActual.agafaDistancia();
                        if (distanciaActualRecursoEnemigo < distanciaMinRecEnemigo) {
                            distanciaMinRecEnemigo = distanciaActualRecursoEnemigo;
                            objRecEnemigoMasCercano = objActual;
                        }
                    }

                    //Salgo de este for con el recurso/agente enemigo y aliado más cercano 
                    //Si no es null significa que está a menos de X píxeles y que no estoy lanzando
                    if (estat.llançaments > 0 && objEnemigoMasCercano != null) {
                        mira(objEnemigoMasCercano);
                        llança();
//                        if (estat.indexNau[CENTRAL] != (100 + estat.id)) {  ///////////////////////////////////////
//                            llança();
//                            System.out.println("pium");
//                        } else {
//                            System.out.println("Quiero disparar pero no puedo");
//                        }
                    }

                    //Si no es null significa que está a menos de X píxeles y que no estoy lanzando
                    if (estat.llançaments > 0 && objRecEnemigoMasCercano != null) {
                        mira(objRecEnemigoMasCercano);
                        llança();
//                        if (estat.indexNau[CENTRAL] != (100 + estat.id)) {  ///////////////////////////////////////
//                            llança();
//                            System.out.println("pium");
//                        } else {
//                            System.out.println("Quiero disparar pero no puedo");
//                        }

                        if (objRecAliadoMasCercano != null) {
                            mira(objRecAliadoMasCercano);
                        }
                    }
                    if (dos_i_tres_buit) {

                    }
                }
            }
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
        }
    }

    private enum Accio {
        ESQUERRA,
        DRETA,
        VOLTEJ,
        ENDAVANT,
    }
}
