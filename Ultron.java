package Ultron;
import robocode.*;
import robocode.util.Utils;
import java.awt.Color;

public class Ultron extends AdvancedRobot
{
	private static final int TURNOS_SEM_VISAO = 3;
    private static final double AFRENTE = Math.toRadians(5);
    private int turnos_depois_visao = TURNOS_SEM_VISAO + 1;


	public void run() {
		setColors(Color.gray,Color.red,Color.red);  

	//Serve para "deslinkar" o radar do resto do tanque
	setAdjustRadarForRobotTurn(true);
	while(true){
		//Ativa a busca por alvos se não encontrar um alvo por muito tempo
	 	if (turnos_depois_visao > TURNOS_SEM_VISAO) {
                	setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
            	}
			turnos_depois_visao++;
			execute();
		}
	}


	public void onScannedRobot(ScannedRobotEvent e) {

	double Distancia = e.getDistance();
	//Calcula a posição atual do inimigo
    double anguloabsoluto= getHeadingRadians() + e.getBearingRadians();
    double XInimigo = getX() + Math.sin(anguloabsoluto) * Distancia;
    double YInimigo = getY() + Math.cos(anguloabsoluto) * Distancia;

	//Garante que a potência não seja menor que 0.1 nem maior que 3
	double ForcaTiro = Math.min(3.0, Math.max(0.1, 300 / e.getDistance()));

	//Tenta Perseguir o alvo com o scan
	turnos_depois_visao = 0;
        double virarScan = Utils.normalRelativeAngle(anguloabsoluto - getRadarHeadingRadians());
        double afrente = Math.copySign(AFRENTE, virarScan);
        setTurnRadarRightRadians(virarScan + afrente);

	//Calcula a posição linear futura e atira nela
	double dx = XInimigo - getX();
	double dy = YInimigo - getY();
	double VelX = e.getVelocity() * Math.sin(e.getHeadingRadians()); 
	double VelY = e.getVelocity() * Math.cos(e.getHeadingRadians());
	double VBala = 20 - (3 * ForcaTiro);
	double tempo = Distancia / VBala;
	double prevX = XInimigo + VelX * tempo;
	double prevY = YInimigo + VelY * tempo;
	double anguloTiro = Math.atan2(prevX - getX(),prevY - getY());
	double virarCanhao = Utils.normalRelativeAngle(anguloTiro - getGunHeadingRadians());
	setTurnGunRightRadians(virarCanhao);
	setFire(ForcaTiro);
	}

	

	public void onHitByBullet(HitByBulletEvent e) {
		setBack(75);
	}
	

	public void onHitWall(HitWallEvent e) {
		setBack(150);
		setTurnRight(90);
		setAhead(100);
	}	
}
