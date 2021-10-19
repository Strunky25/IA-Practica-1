
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

    private static final int MAX_VISIO = 405;
    
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
        int cost = atributsAgent(6, 8, 600, 30, 23, 5, 5);
        System.out.println("Cost total: " + cost);
        // Inicialització de variables que utilitzaré al meu comportament
        repetir = 0;
        accio = Accio.ENDAVANT;
        mirant = false;
        random = new Random();
    }

    @Override
    public void avaluaComportament() {
        if(!repetirAccio()){ 
            estat = estatCombat();
            deteccioRecursos();
            deteccioParet();         
        }
    }
    
    private void deteccioParet(){
        if(estat.enCollisio){
            accio = Accio.VOLTEJ;
            repetir = 1;
        }else if(estat.distanciaVisors[CENTRAL] < 30 &&
                estat.objecteVisor[CENTRAL] == PARET){
            if(random.nextBoolean()) accio = Accio.DRETA;
            else accio = Accio.ESQUERRA;
            repetir = 3;    
        } else if (estat.distanciaVisors[ESQUERRA] < 40 &&
                estat.objecteVisor[ESQUERRA] == PARET){
            accio = Accio.DRETA;
            repetir = 3;
        } else if (estat.distanciaVisors[DRETA] < 40 &&
                estat.objecteVisor[DRETA] == PARET){
            accio = Accio.ESQUERRA;
            repetir = 3;
        } else {
            endavant();
        }
    }
    
    private void deteccioRecursos(){
        if(estat.numObjectes > 0 && estat.veigAlgunRecurs){
            int distanciaMin = MAX_VISIO;
            int distanciaActual;
            int distanciaMinRecAliado = MAX_VISIO;
            int distanciaMinRecEnemigo = MAX_VISIO;
            Objecte objRecAliadoMasCercano = null;
            Objecte objRecEnemigoMasCercano = null;
            for(Objecte objActual: estat.objectes){
                if(objActual != null){
                    distanciaActual = objActual.agafaDistancia();
                    if(distanciaActual < distanciaMin){
                        distanciaMin = distanciaActual;
                        if(esRecursAliat(objActual)){
                            distanciaMinRecAliado = distanciaActual;
                            objRecAliadoMasCercano = objActual;
                        } else if(objActual.agafaTipus() >= 100 
                                && !esRecursAliat(objActual)){
                            distanciaMinRecEnemigo = distanciaActual;
                            objRecEnemigoMasCercano = objActual;
                        }
                    }
                }
            }
            if(distanciaMinRecEnemigo < MAX_VISIO &&
                    estat.llançaments > 0 && !estat.llançant
                    && objRecEnemigoMasCercano != null){
                System.out.println(objRecEnemigoMasCercano.agafaTipus());
                mira(objRecEnemigoMasCercano);
                llança();
                mirant = true;
            }
            /*
            if(distanciaMinRecAliado < MAX_VISIO 
                    && objRecAliadoMasCercano != null){
                mira(objRecAliadoMasCercano);
                mirant = true;
            } else mirant = false;*/
        } else mirant = false;
    }
    
    private boolean esRecursAliat(Objecte obj){
        return obj.agafaTipus() == (100 + estat.id);
    }
    
    private boolean repetirAccio(){
        if(repetir != 0){
            switch(accio){
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
        } else return false;
    }
    
    private void comprobaMirant(){
        if(mirant){
            accio = Accio.DESFER;
            repetir = 1;
        }
    }
    
    private enum Accio{
        ESQUERRA,
        DRETA,
        VOLTEJ,
        ENDAVANT, 
        DESFER
    }
}
