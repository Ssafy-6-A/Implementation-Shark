package test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

/* 마법사상어와 토네이도
 * N*N인 격자의 모레밭에서 (r,c)는 위치, A[r][c]는 위치의 모래양이다.
 * 달팽이모양으로 토네이도가 돈다. 제일 안쪽 가운데부터 시작하는거고.. 한번에 한칸 이동한다.
 * 토네이도가 한칸 이동할때마다 모래는 일정비율로 흩날리게 된다.
 * (1,1)까지 이동한 뒤 소멸한다. 모래가 격자의 밖으로 이동할수있음
 * 토네이도 소멸되었을때 격자 밖으로 나간 모래의 양을 구해보자!
 * 
 * 문제를 아예 잘못 이해했었음.. 이동자체가 토네이도가 아니라 중심이 토네이도고, 그게 달팽이모양으로 돌도록 만드는것이다.
 * 
 * 풀이 : 현재에서 다음위치로 이동하면 다음위치의 모래를 흩뿌려주고 현재위치를 다음위치로 업데이트한다.
 * 끝나는 시점은 이동한 위치가 맵을 벗어나면 멈추는것임!
 * 
 * 토네이도 도는거 1(좌)->1(하)->2(우)->2(상)->3(좌)->3(하)->4(우)->4(상)->5->5->6->6 (좌하우상 순서대로)
 * 즉 좌로 한턴 돌고, 다음좌가 오기까지의 길이를 보면 1->3이 된다. 즉 똑같은 방향을 다시 돌땐 이전보다 +2만큼 더 돌고있다.
 * 
 * >> 어렵네......... 나중에 다시 풀어보기
 */
public class Main {
	static int N, answer, sand[][];
	static int[] percent = {1,1,7,7,10,10,2,2,5};
	static int[] dx = {0,1,0,-1}; //좌,하,우,상 순서로 돈다
	static int[] dy = {-1,0,1,0};
	static int[][] spreadX = { //각 이동방향(좌,우,하,상)에 대한 모래 좌표값 저장
			{-1,1,-1,1,-1,1,-2,2,0,0},
			{0,0,1,1,2,2,1,1,3,2},
			{-1,1,-1,1,-1,1,-2,2,0,0},
			{0,0,-1,-1,-2,-2,-1,-1,-3,-2}
	};
	static int[][] spreadY = {
			{0,0,-1,-1,-2,-2,-1,-1,-3,-2},
			{-1,1,-1,1,-1,1,-2,2,0,0},
			{0,0,1,1,2,2,1,1,3,2},
			{-1,1,-1,1,-1,1,-2,2,0,0}
	};
	
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		N = Integer.parseInt(br.readLine());
		sand = new int[N][N];
		
		for(int i=0; i<N; i++) {
			StringTokenizer st = new StringTokenizer(br.readLine());
			for(int j=0; j<N; j++) sand[i][j] = Integer.parseInt(st.nextToken());
		}
		
		int x = N/2; //한가운데 좌표 설정
		int y = N/2;
		int moveCnt = 1; //1칸이동한다
		int dir = 0;
		
		while(true) {
			for(int i=0; i<2; i++) { //1->1->2->2->3->3, 즉 2번씩 이동 후 카운트가 증가한다.(한칸씩 두번돌고, 두칸씩 세번돌고..)
				for(int j=0; j<moveCnt; j++) {
					spreadSand(x,y,dir);
					x+=dx[dir];
					y+=dy[dir];
				}
				dir = (dir+1)%4; //방향전환
			}
			moveCnt++;
			
			if(moveCnt==N) { // 마지막 6->6->6 중에서 6->6만 돌고 moveCnt가 7이됨, 이제 마지막줄까지 이동 후 소멸시키자
				for(int i=0; i<moveCnt-1; i++) {
					spreadSand(x,y,dir);
					x+=dx[dir];
					y+=dy[dir];
				}
				break;
			}
		}
		
		System.out.println(answer);
	}

	//x,y가 중심점이며 dir방향으로 이동할때 모래를 흩뿌려주는 메소드
	private static void spreadSand(int x, int y, int dir) {
		int nx = x+dx[dir];
		int ny = y+dy[dir];
		int curr = sand[nx][ny];
		
		//모래가 흩뿌려지는 9개 좌표 모두 순회
		for(int i=0; i<9; i++) {
			int nnx = x + spreadX[dir][i];
			int nny = y + spreadY[dir][i];
			
			if(nnx<0 || nny<0 || nnx>=N || nny>=N) answer+=curr*percent[i]/100; //맵의 바깥이라면 answer에 누적
			else sand[nnx][nny] += curr*percent[i]/100; //흩뿌려진 모래 더해줌
			
			sand[nx][ny] -= curr*percent[i]/100; //남은모래
		}
		
		//a에 모래 이동시키기
		int ax = x+spreadX[dir][9];
		int ay = y+spreadY[dir][9];
		if(ax<0||ay<0||ax>=N||ay>=N) answer += sand[nx][ny];
		else sand[ax][ay] += sand[nx][ny]; //a에 남은모래 저장
		sand[nx][ny] = 0; //모두 옮겼으니 현재 위치 0으로 변환
	}
}


