/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kadaigame;

  import java.awt.*;
  import java.awt.event.*;
  import javax.swing.*;
  import java.util.*;

  public class Kadaigame extends JPanel {
    static final int EMPTY = 0, BATSU = 1, MARU = 2;
    static final int YMAX = 6, XMAX = 6;
    ArrayList<Figure> figs = new ArrayList<Figure>();
    ArrayList<Maru>  marus = new ArrayList<Maru>();
    //ArrayList<Maru_color> colors = new ArrayList<Maru_color>();
    int[] colors = new int[36] ;
    
    int sel = -1;
    Color co[] = { Color.blue , 
                   Color.green ,
                   Color.orange , 
                   Color.red , 
                   Color.black,
                   Color.white
		};
    
    //ブロックのマス中心座標
    int[][] masu ={{80,40},{80,80},{80,120},{80,160},{80,200},{80,240},
                   {120,40},{120,80},{120,120},{120,160},{120,200},{120,240},
                   {160,40},{160,80},{160,120},{160,160},{160,200},{160,240},
                   {200,40},{200,80},{200,120},{200,160},{200,200},{200,240},
                   {240,40},{240,80},{240,120},{240,160},{240,200},{240,240},
                   {280,40},{280,80},{280,120},{280,160},{280,200},{280,240}
                   };
    
    int[] drop_maru = new int[36];
    
    boolean turn = true;
    int winner = EMPTY;
    int[][] board = new int[YMAX][XMAX];
    
    Text t1 = new Text(0, 20, "石をドラッグして同じ色の石を３つ以上揃えよう",
                new Font("Serif", Font.BOLD, 20));
    
    
    public Kadaigame() {
      
      //テキスト追加
      figs.add(t1);
      //マスを作る
      for(int i = 0; i < YMAX*XMAX; ++i) {
        int r = i / YMAX, c = i % YMAX;
        figs.add(new Rect(Color.lightGray, 80+r*40, 40+c*40, 38, 38));
        marus.add(new Maru(80+r*40, 40+c*40, 17));
        
        Random rnd = new Random();
        int color_number = rnd.nextInt(5);
        //System.out.println(color_number);
        //colors.add(new Maru_color());
        colors[i] = color_number;
      }
      
      //System.out.println(Arrays.toString(colors));
      setOpaque(false);
      
      addMouseListener(new MouseAdapter() {
        public void mousePressed(MouseEvent evt) {
          int i = 0;
          for(Maru m: marus) { 
            if (m.hit(evt.getX(), evt.getY())){
              sel = i; 
              //System.out.println(i);
              //System.out.println(colors[i]);
            }
            i++;
          }
          /*for(Maru_color mc: colors){
              System.out.println(mc.get(1));
          }*/
        }
        
        public void mouseReleased(MouseEvent evt) {
            System.out.println("離した");
            //一番近いマスを探す
            int mouse_x = evt.getX();
            int mouse_y = evt.getY();
            int min_distance = 999999999;
            int distance;
            int closest_masu =100;
            
            //System.out.println("現在座標"+mouse_x+"/"+mouse_y);
            
            for(int i=0;i<masu.length;++i){
                //ystem.out.println(masu[i][0]+"/"+masu[i][1]);
                int x = masu[i][0];
                int y = masu[i][1];
                distance = (mouse_x-x)*(mouse_x-x)+(mouse_y-y)*(mouse_y-y);
                
                if(min_distance>distance){
                    closest_masu = i;
                    min_distance = distance;
                    //System.out.println("ok");
                }
                
                //System.out.println(i+"/"+distance);
                //System.out.println(closest_masu);
            }
            
            //System.out.println(closest_masu);
            //System.out.println(min_distance);
            
            //そのマスのところへ移動する
            int to_x = masu[closest_masu][0];
            int to_y = masu[closest_masu][1];
            marus.get(sel).moveTo(to_x, to_y);
            repaint();
            
            //3つ以上揃ってるところを判定する
            //縦のラインをまずちぇっく
            int[][] result_vertical   = line_ckeck_vertical(colors);
            int[][] result_horizontal = line_ckeck_horizontal(colors);
            
            //System.out.println(Arrays.deepToString(result_vertical));
            //System.out.println(Arrays.deepToString(result_horizontal));
            
            //System.out.println("----");
            list_delete_maru(result_vertical);
            list_delete_maru(result_horizontal);
            
            //石を消す
            for(int d_i=0;d_i<drop_maru.length;++d_i){
                int delete_flag = drop_maru[d_i];
                
                if(delete_flag==1){
                    colors[d_i]=5;
                    //marus.set(d_i,null);
                    System.out.println(d_i);
                }
                
            }
            //repaint();
        }
        
        //縦のラインチェック
        public int[][] line_ckeck_vertical(int[] colors){
            int[][] result = new int[10][7];
            int prior_color = -1;
            int count = 0;
            int combo = 0;
            int hoge =0;
            
            for(int i=0;i<6;++i){
                for(int j=0;j<6;++j){
                    int pointer = j+6*i;
                    
                    //System.out.println(pointer+"/"+colors[pointer]);
                    
                    if(prior_color==-1){
                        prior_color = colors[pointer];
                        count++;
                        
                        //System.out.println("first");
                    }else{
                        //System.out.println("second");
                        
                        //前の色と同じなら
                        if(prior_color==colors[pointer]){
                            count++;
                            //System.out.println("前と同じ色");
                            
                        //前の色と違うけど、既に3つ以上揃っていれば
                        }else if(prior_color!=colors[pointer]&&count>=3){
                            //System.out.println("違う色、コンボ有り");
                            //System.out.println("コンボ");
                            int tmp_p = pointer-1;
                            
                            //System.out.println("配列追加");
                            result[combo][0] =count;
                            for(int k=1;k<=count;++k){
               
                                result[combo][k] = tmp_p;
                                //System.out.println(tmp_p);
                                tmp_p--;

                            }
                            combo++;
                            count = 1;
                            prior_color = colors[pointer];
                            
                        
                        //前の色と違うかつ、3つ以上揃ってもいない
                        }else if(prior_color!=colors[pointer]&&count<3){
                   
                            count = 1;
                            prior_color = colors[pointer];
                            //System.out.println("違う色、コンボ無し");
                        }
                        
                    }
                   
                    hoge = pointer;
                }
                //3つ以上揃っていれば
                if(count>=3){
                    //System.out.println("配列追加");
                       
                    int tmp_p = hoge;
                    result[combo][0] = count;
                    for(int k=1;k<=count;++k){
                        result[combo][k] = tmp_p;
                        //System.out.println(tmp_p);
                        tmp_p--;
                            
                    }
                    combo++;
                }
                
                count = 0;
                prior_color = -1;
                //System.out.println("---------------");
                    
            }
            
            //System.out.println(Arrays.deepToString(result));
            
            return result;
        }
        
        public int[][] line_ckeck_horizontal(int[] colors){
            int[][] result = new int[10][7];
            int prior_color = -1;
            int count = 0;
            int combo = 0;
            int hoge =0;
            
            for(int i=0;i<6;++i){
                for(int j=0;j<6;++j){
                    int pointer = i+6*j;
                    
                    //System.out.println(pointer+"/"+colors[pointer]);
                    
                    if(prior_color==-1){
                        prior_color = colors[pointer];
                        count++;
                        
                        //System.out.println("first");
                    }else{
                        //System.out.println("second");
                        
                        //前の色と同じなら
                        if(prior_color==colors[pointer]){
                            count++;
                            //System.out.println("前と同じ色");
                            
                        //前の色と違うけど、既に3つ以上揃っていれば
                        }else if(prior_color!=colors[pointer]&&count>=3){
                            //System.out.println("違う色、コンボ有り");
                            //System.out.println("コンボ");
                            int tmp_p = pointer-6;
                            
                            //System.out.println("配列追加");
                            result[combo][0] =count;
                            for(int k=1;k<=count;++k){
               
                                result[combo][k] = tmp_p;
                                //System.out.println(tmp_p);
                                tmp_p-=6;

                            }
                            combo++;
                            count = 1;
                            prior_color = colors[pointer];
                            
                        
                        //前の色と違うかつ、3つ以上揃ってもいない
                        }else if(prior_color!=colors[pointer]&&count<3){
                   
                            count = 1;
                            prior_color = colors[pointer];
                            //System.out.println("違う色、コンボ無し");
                        }
                        
                    }
                   
                    hoge = pointer;
                }
                //3つ以上揃っていれば
                if(count>=3){
                    //System.out.println("配列追加");
                       
                    int tmp_p = hoge;
                    result[combo][0] = count;
                    for(int k=1;k<=count;++k){
                        result[combo][k] = tmp_p;
                        //System.out.println(tmp_p);
                        tmp_p-=6;
                            
                    }
                    combo++;
                }
                
                count = 0;
                prior_color = -1;
                //System.out.println("---------------");
                    
            }
            
            //System.out.println(Arrays.deepToString(result));
            
            return result;
        }
        
        //消す丸をまとめる
        public void list_delete_maru(int[][] result){
            for(int i=0;i<result.length;++i){
                int roop_count = result[i][0];
                
                if(roop_count==0){ break; }
                
                for(int j=1;j<=roop_count;++j){
                    //System.out.println(result[i][j]);
                    drop_maru[result[i][j]]=1;
                }
                
            }
        }
        
      });
      
      
      addMouseMotionListener(new MouseMotionAdapter() {
        public void mouseDragged(MouseEvent evt) {
          //どの円も選択されていない
          if (sel==-1) { return; }
          
          int mouse_x = evt.getX();
          int mouse_y = evt.getY();
          int to_x = masu[sel][0];
          int to_y = masu[sel][1];
          
          //斜めのマスへ行けなくする
          if(mouse_x<to_x-20 && mouse_y<to_y-20){
              return;
          }else if(mouse_x>to_x+20 && mouse_y<to_y-20){
              return;
          }else if(mouse_x>to_x+20 && mouse_y>to_y+20){
              return;
          }else if(mouse_x<to_x-20 && mouse_y>to_y+20){
              return;
          }
          
          marus.get(sel).moveTo(evt.getX(), evt.getY());
          //System.out.println(evt.getX()+"/"+evt.getY());
          
          //近い円と位置を交換する
          int sel_up   = sel-1;
          int sel_right= sel+6;
          int sel_down = sel+1;
          int sel_left = sel-6;
          
          System.out.println("セル"+sel);
          System.out.println(sel_up+"/"+sel_right+"/"+sel_down+"/"+sel_left);
          
          int[] next_to_masu = {sel_up,
                                sel_right,
                                sel_down,
                                sel_left};
          
          for(int i=0;i<next_to_masu.length;++i){
              if(next_to_masu[i]>=0 && next_to_masu[i]<36){
                  //System.out.println("ok"+next_to_masu[i]);
                  
                  int x = masu[next_to_masu[i]][0];
                  int y = masu[next_to_masu[i]][1];
                  int move_maru = next_to_masu[i];
                  //int to_x = masu[sel][0];
                  //int to_y = masu[sel][1];
                  int distance = (mouse_x-x)*(mouse_x-x)+(mouse_y-y)*(mouse_y-y);
                  
                  //System.out.println(move_maru+"との距離"+distance);
                  
                  if(distance<410){
                      marus.get(move_maru).moveTo(to_x, to_y);
                      //System.out.println("移動"+to_x+"/"+to_y);
                      swap(marus,colors,sel,move_maru);
                      sel = move_maru;
                      break;
                  }
              }
          }
          
          repaint();
          
        }
        
        //入れ替え
        public <t> void swap(ArrayList<t> list, int[] colors, int index1,int index2){
            
            //arrayListの入れ替え
            t tmp=list.get(index1);
            list.set(index1,list.get(index2));
            list.set(index2, tmp);
            
            //色の配列入れ替え
            int color_tmp = colors[index1];
            colors[index1] = colors[index2];
            colors[index2] = color_tmp;
 
        }
        
      });
      
    }
    
    /*
    public Rect pick(int x, int y) {
      Rect r = null;
      for(Figure f: figs) {
        if(f instanceof Rect && ((Rect)f).hit(x, y)) {
          r = (Rect)f;
        }
      }
      return r;
    }
    private int ck(int x, int y, int dx, int dy) {
      int s = board[y][x], count = 1;
      for(int i = 1; ck1(x+dx*i, y+dy*i, s); ++i) { ++count; }
      for(int i = 1; ck1(x-dx*i, y-dy*i, s); ++i) { ++count; }
      return count;
    }
    private boolean ck1(int x, int y, int s) {
      return 0<=x && x<XMAX && 0<=y && y<YMAX && board[y][x]==s;
    }
    
    */
    
    //画像を描く
    public void paintComponent(Graphics g) {
      int i = 0;
      for(Figure f: figs) { f.draw(g); }
      for(Maru m: marus) {
          int m_color = colors[i];
          m.draw(g,m_color);
          i++;
      }
    }
    
    public static void main(String[] args) {
      JFrame app = new JFrame();
      app.add(new Kadaigame());
      app.setSize(500, 400);
      app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      app.setVisible(true);
    }
    
    interface Figure {
      public void draw(Graphics g);
    }
    
    static abstract class SimpleFigure implements Figure {
      int xpos, ypos;
      public SimpleFigure(int x, int y) { xpos=x; ypos=y; }
      public void moveTo(int x, int y) { xpos=x; ypos=y; }
      public void draw(Graphics g) {
        //g.setColor(Color.RED);
        ((Graphics2D)g).setStroke(new BasicStroke(4));
      }
    }
    
    static class Text implements Figure {
      int xpos, ypos;
      String txt;
      Font fn;
      public Text(int x, int y, String t, Font f) {
        xpos = x; ypos = y; txt = t; fn = f;
      }
      public void setText(String t) { txt = t; }
      public void draw(Graphics g) {
        g.setColor(Color.BLACK); g.setFont(fn);
        g.drawString(txt, xpos, ypos);
      }
    }
    
    static class Maru extends SimpleFigure {
      int size;
      public Maru(int x, int y, int s) {super(x,y);size=s;}
      public void draw(Graphics g, int color_num) {
        //Random rnd = new Random();
        //int color_number = rnd.nextInt(5); 
        Color co[] = { Color.blue , 
                        Color.green ,
			Color.orange , 
			Color.red , 
			Color.black,
                        Color.white
		};
        //System.out.println(color_number);
        g.setColor(co[color_num]);
        super.draw(g);
        g.fillOval(xpos-size, ypos-size, 2*size, 2*size);
      }
      
      //マウスクリックの座標値がこの円の内部か確認する
      public boolean hit(int x, int y){
          return (xpos-x)*(xpos-x)+(ypos-y)*(ypos-y)<=size*size;          
      }
    }
    
    static class Batsu extends SimpleFigure {
      int size;
      public Batsu(int x, int y, int s) {super(x,y);size=s;}
      public void draw(Graphics g) {
        super.draw(g);
        g.drawLine(xpos-size, ypos-size, xpos+size, ypos+size);
        g.drawLine(xpos-size, ypos+size, xpos+size, ypos-size);
      }
    }
    
    static class Rect extends SimpleFigure {
      Color col;
      int width, height;
      public Rect(Color c, int x, int y, int w, int h) {
        super(x, y); col = c; width = w; height = h;
      }
      public boolean hit(int x, int y) {
        return xpos-width/2 <= x && x <= xpos+width/2 &&
               ypos-height/2 <= y && y <= ypos+height/2;
      }
      public int getX() { return xpos; }
      public int getY() { return ypos; }
      public void draw(Graphics g) {
        g.setColor(col);
        g.fillRect(xpos-width/2,ypos-height/2,width,height);
      }
    }
    
    /*static class Drop{
        //縦のラインチェック
        public int li(){
            int[][] result;
            for(int i=0;i<6;++i){
                for(int j=0;j<6;++j){
                int pointer = j+6*i;
                }
            }
            return 1;
        }
        
    }*/
    
    /*
    static class Maru_color{
        //int color_num;
        public int Maru_color(){
            Random rnd = new Random();
            int color_number = rnd.nextInt(5);
            //System.out.println(color_number);
            return color_number;
        }
    }
    */
  }
