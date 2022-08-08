package com.senior.util;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class CreateVerificationCode {

    private String code;
    private Graphics g;

    /**
     * 获取随机生成的颜色
     *
     * @param s
     * @param e
     * @return
     */
    public Color getRandColor(int s, int e) {

        Random random = new Random();
        if (s > 255) {
            s = 91;
        }
        if (e > 255) {
            e = 97;
        }

        int r, g, b;
        r = s + random.nextInt(e - s);
        g = s + random.nextInt(e - s);
        b = s + random.nextInt(e - s);
        return new Color(r, g, b);

    }

    /**
     * 获取验证码图片
     */
    public BufferedImage getIdentifyImg() {

        int width = 100;
        int height = 28;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 创建Graphics对象,相当于画笔
        Graphics g = image.getGraphics();
        // 创建Graphics2D对象
        Graphics2D g2d = (Graphics2D) g;
        Random random = new Random();
        // 定义字体样式
        Font font = new Font("华文宋体", Font.BOLD, 19);
        g.setColor(this.getRandColor(200, 250));
        g.fillRect(0, 0, width, height);
        g.setFont(font);
        g.setColor(this.getRandColor(180, 200));

        // 绘制100条颜色和位置全部随机的线条 该线条为2f
        for (int i = 0; i < 100; i++) {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);
            int x1 = random.nextInt(6) + 1;
            int y1 = random.nextInt(12) + 1;
            // 绘制线条样式
            BasicStroke bs = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
            Line2D line = new Line2D.Double(x, y, x1 + x, y1 + y);
            g2d.setStroke(bs);
            g2d.draw(line);

        }

        // 输出由英文中文数字随机组成的验证码
        StringBuilder sRand = new StringBuilder();
        String ctmp = "";
        int itmp = 0;
        for (int i = 0; i < 4; i++) {
            switch (random.nextInt(3)) {
                case 1:
                case 2:
                    itmp = random.nextInt(26) + 65;
                    ctmp = String.valueOf((char) itmp);
                    break;
                default:
                    itmp = random.nextInt(10) + 48;
                    ctmp = String.valueOf((char) itmp);
                    break;
            }
            sRand.append(ctmp);
            Color color = new Color(20 + random.nextInt(110), 20 + random.nextInt(110), random.nextInt(110));
            g.setColor(color);
            g.drawString(ctmp, 19 * i + 19, 19);
        }
        this.setCode(sRand.toString());
        this.setG(g);
        return image;
    }
}
