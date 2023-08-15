
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;


/* 20056. 마법사상어와 파이어볼
 * N*N격자에 파이어볼 M개를 발사, 가장 처음엔 파이어볼이 각 위치에서 이동대기중
 * i번 파이어볼의 위치는 (r,c) 질량은 m, 방향은 d, 속력은 s임
 * 행과열은 1번부터 N번까지 있으며 1번행은N번, 1번열은 N번과 연결되어있음 -> 뭔솔?????????? -> 맵의 끝이 없다는얘기래
 * 
 * 파이어볼 방향 의미
 * 7 0 1
 * 6 볼 2
 * 5 4 3
 * 
 * 1. 모든 파이어볼이 방향d로 속력s만큼 이동(이동중에 같은칸에 여러개 파이어볼 존재 가능)
 * 2. 이동 끝난뒤 2개 이상 파이어볼이 있을경우 (1)같은칸 파이어볼은 합쳐짐 (2)파이어볼은 4개 파이어볼로 나뉨
 * 3. 나눠진 파이어볼의 질량, 속력, 방향은 다음과 같다
 * 		(1) 질량은 합쳐진파이어볼질량합/5
 * 		(2) 속력은 합쳐진파이어볼질량합/합쳐진파이어볼갯수
 * 		(3) 합쳐지는 파이어볼의 방향이 모두 홀수거나 짝수이면 방향은 0,2,4,6이 되고, 그렇지않으면 1,3,5,7이 됨
 * 4. 질량이 0인 파이어볼은 소멸되어 없어진다. 마법사상어가 이동을 K번 명령한 후, 남은 파이어볼 질량 합을 구해보자
 * 
 * 풀이 : 각 턴마다 이동된 파이어볼의 목록을 저장하는 리스트와 그 내용이 반영된 map을 만들면 될듯..
 */
public class Main {
	//static FireBall[][] map; -> 이렇게 만들면 두개 이상의 파이어볼이 들어가있는 상태표현 불가능
	static int N,M,K;
	static ArrayList<Fireball> map[][];
	static ArrayList<Fireball> fbList;
	static int[] dx = {-1,-1,0,1,1,1,0,-1};
	static int[] dy = {0,1,1,1,0,-1,-1,-1};

	static class Fireball{
		int r,c,m,s,d;
		public Fireball(int r, int c, int m, int s, int d) {
			this.r = r; //행
			this.c = c; //열
			this.m = m; //질량
			this.s = s; //속력
			this.d = d; //방향
		}
		@Override
		public String toString() {
			return this.r+", "+this.c+", "+this.m+", "+this.s+", "+this.d;
		}
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken()); //map크기
		M = Integer.parseInt(st.nextToken()); //파이어볼갯수
		K = Integer.parseInt(st.nextToken()); //이동명령횟수
		map = new ArrayList[N][N];
		//map 초기화
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) map[i][j] = new ArrayList<Fireball>();
		}

		//파이어볼 정보 입력 - r,c,m,s,d(위치,질량,속력,방향)
		for(int i=0; i<M;  i++) {
			st = new StringTokenizer(br.readLine());
			Fireball ball = new Fireball(Integer.parseInt(st.nextToken())-1, Integer.parseInt(st.nextToken())-1, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
			map[ball.r][ball.c].add(ball);
		}

		while(K-->0) move();

		// 질량의 합 출력
		System.out.println(sumM());
	}

	private static int sumM() {
		int answer = 0;
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				for(int k=0; k<map[i][j].size(); k++) {
					answer += map[i][j].get(k).m;
				}
			}
		}
		return answer;
	}

	//한번 이동명령을 내릴때마다 한번씩 실행됨
	private static void move() {
		fbList = new ArrayList<Fireball>();
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				while(map[i][j].size()!=0) { //현재탐색map의 fbList가 비어있지 않다면
					Fireball curr = map[i][j].remove(0); //현재 파이어볼 좌표 이동시키기
					goNext(curr);
				}
			}
		}
		updateMap(fbList);
		divideFireball();
	}

	//방향벡터에 방향값 넣어뒀으므로 그 인덱스를 d를 이용하여 쓰면됨, 속력 s만큼 이동
	//또한 %N을 이용해서 예외처리를 간소화한다. - 어차피 map밖으로 나가는거 아니면 %했을때 안나뉘니까 값 그대로인 성질을 이용
	//ex.1 (0,0)의 파이어볼이(-1,0)이 아닌 (4,0)으로 이동 = 음수 나올경우 -> %나누고 N더하면됨
	//ex2. (4,0)의 파이어볼은 (6,0)이 아닌 (1,0)으로 이동 = N이상 양수 나올경우 -> %로 나누면 됨

	//map밖으로 나가지 못하도록 이동했을때 좌표를 처리해서 fbList에 넣어주는 메소드
	private static void goNext(Fireball curr) {
		int nx = (curr.r + (dx[curr.d]*curr.s))%N;
		int ny = (curr.c + (dy[curr.d]*curr.s))%N;

		if(nx<0) nx = nx+N; //즉, map의 위로 나갔을때
		if(ny<0) ny = ny+N; //map의 왼쪽으로 나갔을때

		fbList.add(new Fireball(nx, ny, curr.m, curr.s, curr.d));
	}

	//한 턴을 마친 fbList를 토대로 맵을 업데이트 해주기
	private static void updateMap(ArrayList<Fireball> tmpList) {
		for(int i=0; i<tmpList.size(); i++) {
			Fireball curr = tmpList.get(i);
			map[curr.r][curr.c].add(curr);
		}
	}

	//여러개의 파이어볼 나눠주기 - map돌면서 2 이상의 파이어볼있으면 나눠준다
	private static void divideFireball() {
		fbList = new ArrayList<>();
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				if(map[i][j].size()>=2) {
					int nm = 0;
					int ns = 0;
					int odd = 0;
					int even = 0;
					int len = map[i][j].size();

					while(map[i][j].size() != 0) {
						Fireball ball = map[i][j].remove(0);
						nm += ball.m;
						ns += ball.s;
						if(ball.d%2==0) even++;
						else odd++;
					}

					if(nm<5) continue;
					else {
						ns /= len;
						if(even==len || odd==len) {
							for(int k=0; k<4; k++) {
								fbList.add(new Fireball(i, j, nm/5, ns, k*2));
							}
						} else {
							for(int k=0; k<4; k++) {
								fbList.add(new Fireball(i, j, nm/5, ns, k*2+1));
							}
						}
					}
				}
			}
		}
		updateMap(fbList);
	}
}
