import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class main {
	public static void main(String[] args) {
		System.out.println("Hello World!");

		List<String> filenamelist = new ArrayList<String>();

		List<ImagePostion> imagespostion = null;
		List<String> imagesname = null;
		imagespostion = new ArrayList<ImagePostion>();
		imagesname = new ArrayList<String>();

		// find all files in dir
		File f = new File(".");
		// System.out.println(f.getName());
		if (f.isDirectory()) {
			File files[] = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().endsWith(".plist")) {
					// System.out.println(files[i].getName());
					filenamelist.add(files[i].getName().substring(0,
							files[i].getName().length() - 6));
				}
			}
		} else {
			System.out.println("This is a file!");
		}

		Iterator<String> itf = filenamelist.iterator();
		while (itf.hasNext()) {
			String plisname = itf.next();
			
			imagespostion.clear();
			imagesname.clear();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(plisname + ".plist");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			SAXParserFactory factorys = SAXParserFactory.newInstance();
			SAXParser saxparser = null;

			try {
				saxparser = factorys.newSAXParser();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			PlistHandler plistHandler = new PlistHandler();

			try {
				saxparser.parse(fis, plistHandler);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			HashMap<String, Object> hash = plistHandler.getMapResult();
			HashMap<String, Object> frames = (HashMap<String, Object>) hash
					.get("frames");
			// System.out.print(frames.toString());
			for (Iterator<String> i = frames.keySet().iterator(); i.hasNext();) {
				String key = i.next();
				// System.out.println(key);
				imagesname.add(key);
				HashMap<String, Object> value = (HashMap<String, Object>) frames
						.get(key);
				String frame = (String) value.get("frame");
				// System.out.println(frame);

				String sarray[] = frame.split(",");
				String x = sarray[0].substring(2);
				String y = sarray[1].substring(0, sarray[1].length() - 1);
				String width = sarray[2].substring(1);
				String hight = sarray[3].substring(0, sarray[3].length() - 2);
				// System.out.println(x+","+y+","+width+","+hight);

				imagespostion.add(new ImagePostion(Integer.parseInt(x), Integer
						.parseInt(y), Integer.parseInt(width), Integer
						.parseInt(hight)));

			}

			try {
				BufferedImage img = ImageIO.read(new File(plisname+".png"));
				// int half_w = img.getWidth() / 2;
				// int rgb[] = new int[half_w * img.getHeight()];
				// img.getRGB(0, 0, half_w, img.getHeight(), rgb, 0, half_w);
				// BufferedImage img_half = new BufferedImage(half_w,
				// img.getHeight(),
				// BufferedImage.TYPE_INT_ARGB);
				// img_half.setRGB(0, 0, half_w, img.getHeight(), rgb, 0,
				// half_w);

				File f6 = new File(plisname);
				f6.mkdir();
				// 保存到新文件half.png里面
				// ImageIO.write(img_half, "PNG", new
				// File(f6.getName()+"\\"+"half.png"));

				Iterator<ImagePostion> it = imagespostion.iterator();
				Iterator<String> itname = imagesname.iterator();
				while (it.hasNext()) {
					// System.out.println(it.next());
					ImagePostion mImagePostion = it.next();
					String mImageName = itname.next();

				//	System.out.println(mImagePostion.x);
				//	System.out.println(mImagePostion.y);
				//	System.out.println(mImagePostion.width);
				//	System.out.println(mImagePostion.hight);
				//	System.out.println(img.getHeight());
				//	System.out.println(img.getWidth());
				//	System.out.println("====");
					
					if(mImagePostion.x+mImagePostion.width>img.getWidth() ||
							mImagePostion.y+mImagePostion.hight>img.getHeight())
					{
						System.out.println("error frame:");
						System.out.println(plisname);
						System.out.println(mImageName);
							System.out.println(mImagePostion.x);
							System.out.println(mImagePostion.y);
							System.out.println(mImagePostion.width);
							System.out.println(mImagePostion.hight);
						break;
					}
					
					int rgb[] = new int[mImagePostion.hight
							* mImagePostion.width];
					img.getRGB(mImagePostion.x, mImagePostion.y,
							mImagePostion.width, mImagePostion.hight, rgb, 0,
							mImagePostion.width);
					BufferedImage img_half = new BufferedImage(
							mImagePostion.width, mImagePostion.hight,
							BufferedImage.TYPE_INT_ARGB);
					img_half.setRGB(0, 0, mImagePostion.width,
							mImagePostion.hight, rgb, 0, mImagePostion.width);
					ImageIO.write(img_half, "PNG", new File(f6.getName() + "\\"
							+ mImageName));
					
					//free();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// ArrayList<Object> array = (ArrayList<Object>) plistHandler
		// .getArrayResult();
	}

}