package test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

/* 마법사상어와 파이어스톰
 * 파이어스톰을 크기가 2^N*2^N인 격자로 나누어진 얼음판에서 연습
 * A(r,c)는 r,c에있는 얼음의 양
 * 시전할때마다 단계 L을 결정해야한다. 파이어스톰은 먼저 격자를 2^L*2^L로 나눈 후 모든 부분격자를 시계방향으로 90도 회전
 * 이후 얼음있는 칸 3개 또는 그 이상과 인접해있지 않은 칸은 얼음양이 1 줄어든다. 즉 0,1,2개만 인접해있으면 얼음양-1
 * (r,c)와 인접한 칸은 (r-1, c), (r+1, c), (r, c-1), (r, c+1)
 * 정수는 칸을 구분하기 위해 적은 정수이다.
 * 
 * 마법사상어는 파이어스톰을 총 Q번 시전한다. 모든 파이어스톰을 시전한 후 다음을 구해보자
 * 1. 남아있는 얼음 A[r][c]의 합
 * 2. 남아있는 얼음 중 가장 큰 덩어리가 차지하는 칸의 갯수(0이아닌 연결된 칸의 집합)
 * 
 * 풀이 : 90도 회전 => 0열이었던게 역순으로 1열로 들어감 즉 tmp[i][j] = arr[N-1-j][i]; 메소드 만들어두고
 * 단계 L에 맞춰서 90도 회전한 (1)분할배열을 구하고 (2)인접 0~2개면 얼음 줄이고 (3)얼음의 합과 (4)큰덩어리 체크 -> 각자 메소드 구하기?
 */
public class Main {
	static int N, Q, map[][], total, bundle;
	static int[] dx = {-1,1,0,0};
	static int[] dy = {0,0,-1,1};
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = (int) Math.pow(2,Integer.parseInt(st.nextToken()));
		Q = Integer.parseInt(st.nextToken()); //L의 갯수
		
		map = new int[N][N];
		for(int i=0; i<N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=0; j<N; j++) map[i][j] = Integer.parseInt(st.nextToken());
		}
		
		st = new StringTokenizer(br.readLine());
		int[] L = new int[Q];
		for(int i=0; i<Q; i++) L[i] = Integer.parseInt(st.nextToken());
		for(int i=0; i<Q; i++) {
			map = divideMap(L[i]);
			map = deleteIce();
		}
		bundle = 0; 
		total = 0;
		
		getBundle();

		System.out.println(total);
		System.out.println(bundle);
	}

	//파라미터로 L을 받아서 2^L*2^L단위로 분할한 map인 tmp를 만드는 메소드
	private static int[][] divideMap(int L) {
		int[][] tmp = new int[N][N];
		L = (int)Math.pow(2, L);
		
		//L단위로 분할하여 turn90한다
		for(int i=0; i<N; i+=L) {
			for(int j=0; j<N; j+=L) {
				turn90(i,j,L,tmp);
			}
		}
		return tmp;
	}

	private static void turn90(int x, int y, int L, int[][] tmp) {
		for(int i=0; i<L; i++) {
			for(int j=0; j<L; j++) {
				//tmp[i][j] = arr[L-1-j][i]였으니까 x,y를 기준점으로 만드려면 다음과 같다.
				tmp[x+i][y+j] = map[x+L-1-j][y+i];
			}
		}
	}

	//전체요소를 탐색하면서 조건 만족시 아이스 -1해줌
	private static int[][] deleteIce() {
		int[][] tmp = new int[N][N];
		for(int i=0; i<N; i++) tmp[i] = Arrays.copyOf(map[i], N);
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				int cnt = 0;
				if(map[i][j]==0) continue;
				for(int k=0; k<4; k++) {
					int nx = i+dx[k];
					int ny = j+dy[k];
					if(nx>=0&&nx<N&&ny>=0&&ny<N&&map[nx][ny]>0) cnt++;
				}
				if(cnt<3) tmp[i][j]--; //주변 ice의 갯수가 3미만이면 -1 해준다
			}
		}
		return tmp;
	}

	//가장 넓은 영역을 구해준다
	private static void getBundle() {
		Queue<int[]> q = new LinkedList<int[]>();
		boolean[][] visit = new boolean[N][N];
		
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				total += map[i][j]; //전체 얼음수 같이 구해준다
				if(map[i][j]>0 && !visit[i][j]) { //가장넓은 영역의 경우 bfs해서 구한다
					q.add(new int[] {i,j});
					visit[i][j] = true;
					int cnt = 1;
					
					while(!q.isEmpty()) {
						int[] t = q.poll();
						int tx = t[0];
						int ty = t[1];
						
						for(int k=0; k<4; k++) {
							int nx = tx+dx[k];
							int ny = ty+dy[k];
							if(nx>=0 && nx<N && ny>=0 && ny<N 
									&& map[nx][ny]>0 && !visit[nx][ny]) {
								visit[nx][ny] = true;
								q.add(new int[] {nx,ny});
								cnt++;
							}
						}
					}
					bundle = Math.max(bundle, cnt);
				}
			}
		}
		
	}
}


