package raychaser;

/*
* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
*
* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
*
* The contents of this file are subject to the terms of either the GNU
* General Public License Version 2 only ("GPL") or the Common
* Development and Distribution License("CDDL") (collectively, the
* "License"). You may not use this file except in compliance with the
* License. You can obtain a copy of the License at
* http://www.netbeans.org/cddl-gplv2.html
* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
* specific language governing permissions and limitations under the
* License.  When distributing the software, include this License Header
* Notice in each file and include the License file at
* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
* particular file as subject to the "Classpath" exception as provided
* by Sun in the GPL Version 2 section of the License file that
* accompanied this code. If applicable, add the following below the
* License Header, with the fields enclosed by brackets [] replaced by
* your own identifying information:
* "Portions Copyrighted [year] [name of copyright owner]"
*
* Contributor(s): Alexandre Iline.
*
* The Original Software is the Jemmy library.
* The Initial Developer of the Original Software is Alexandre Iline.
* All Rights Reserved.
*
* If you wish your version of this file to be governed by only the CDDL
* or only the GPL Version 2, indicate your decision by adding
* "[Contributor] elects to include this software in this distribution
* under the [CDDL or GPL Version 2] license." If you do not indicate a
* single choice of license, a recipient has the option to distribute
* your version of this file under either the CDDL, the GPL Version 2 or
* to extend the choice of license to its licensees as provided above.
* However, if you add GPL Version 2 code and therefore, elected the GPL
* Version 2 license, then the option applies only if the new code is
* made subject to such option by the copyright holder.
*
*
*
* $Id$ $Revision$ $Date$
*
*/

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

/** This class allows to encode JavaFX Image to color PNG
* image format with maximum compression.<br>
* Original author: Adam Sotona<br>
* Changes made by me include:<br>
* - Removed options for b/w and grayscale images<br>
* - Removed static functions for snapshot etc.<br>
* - Now works with JavaFX Image instead of BufferedImage<br>
* @author Adam Sotana, Karl Eknefelt
* @version 1.0 */
public class PNGEncoder extends Object {
  OutputStream out;
  CRC32 crc;

  /** public constructor of PNGEncoder class.
  * @param out output stream for PNG image format to write into
  */
  public PNGEncoder(OutputStream out) {
    crc = new CRC32();
    this.out = out;
  }

  void write(int i) throws IOException {
    byte b[]={(byte)((i>>24)&0xff),(byte)((i>>16)&0xff),(byte)((i>>8)&0xff),(byte)(i&0xff)};
    write(b);
  }

  void write(byte b[]) throws IOException {
    out.write(b);
    crc.update(b);
  }

  /** main encoding method (stays blocked till encoding is finished).
  * @param image Image to encode
  * @throws IOException IOException
  */
  public void encode(Image image) throws IOException {
    int width = (int)image.getWidth();
    int height = (int)image.getHeight();
    final byte id[] = {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13};
    write(id);
    crc.reset();
    write("IHDR".getBytes());
    write(width);
    write(height);
    final byte head[] = {8, 2, 0, 0, 0};
    write(head);
    write((int) crc.getValue());
    ByteArrayOutputStream compressed = new ByteArrayOutputStream(65536);
    BufferedOutputStream bos = new BufferedOutputStream( new DeflaterOutputStream(compressed, new Deflater(9)));
    PixelReader px_reader = image.getPixelReader();
    int pixel;
    int color;
    int colorset;

    //Write the color data
    for (int y=0;y<height;y++) {
      bos.write(0);
      for (int x=0;x<width;x++) {
        pixel=px_reader.getArgb(x,y);
        bos.write((byte)((pixel >> 16) & 0xff));
        bos.write((byte)((pixel >> 8) & 0xff));
        bos.write((byte)(pixel & 0xff));
      }
    }

    bos.close();
    write(compressed.size());
    crc.reset();
    write("IDAT".getBytes());
    write(compressed.toByteArray());
    write((int) crc.getValue());
    write(0);
    crc.reset();
    write("IEND".getBytes());
    write((int) crc.getValue());
    out.close();
  }
}
