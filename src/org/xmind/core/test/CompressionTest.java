/* ******************************************************************************
 * Copyright (c) 2006-2012 XMind Ltd. and others.
 * 
 * This file is a part of XMind 3. XMind releases 3 and
 * above are dual-licensed under the Eclipse Public License (EPL),
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 * and the GNU Lesser General Public License (LGPL), 
 * which is available at http://www.gnu.org/licenses/lgpl.html
 * See http://www.xmind.net/license.html for details.
 * 
 * Contributors:
 *     XMind Ltd. - initial API and implementation
 *******************************************************************************/
package org.xmind.core.test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;

import org.xmind.core.Core;
import org.xmind.core.CoreException;
import org.xmind.core.INotes;
import org.xmind.core.INotesContent;
import org.xmind.core.IPlainNotesContent;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;
import org.xmind.core.internal.zip.ZipStreamOutputTarget;

@SuppressWarnings("nls")
public class CompressionTest {

    private static final Random rand = new Random();

    private static final byte[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 "
            .getBytes();
    IWorkbook workbook = Core.getWorkbookBuilder().createWorkbook();
    public CompressionTest() {
    }

    private void createWorkbookContents(IWorkbook workbook) {
        ITopic root = workbook.getPrimarySheet().getRootTopic();
        root.setTitleText("Shell scripts");
        File[] files = new File("/Users/davidhartman/test").listFiles();
        //initialize curTopic to Root
        System.out.println("root topic id:"+root.getId());
        addTopics(root,files);
        
//        ITopic topic1 = workbook.createTopic();
//        topic1.setTitleText("Topic1");
//        root.add(topic1);
//        ITopic topic2 = workbook.createTopic();
//        topic2.setTitleText("Topic2");
//        
//        ITopic childTopic1 = workbook.createTopic();
//        childTopic1.setTitleText("childTopic1");
//        topic1.add(childTopic1);
//        
//        root.add(topic2);
        
        
        /* for (int i = 0; i < 10; i++) {
            ITopic topic = workbook.createTopic();
            topic.setTitleText("Topic " + i);
            INotesContent notes = workbook.createNotesContent(INotes.PLAIN);
            ((IPlainNotesContent) notes)
                    .setTextContent(createRandomString(256));
            topic.getNotes().setContent(INotes.PLAIN, notes);
            root.add(topic);
        }
        */
    }

    private void addTopics(ITopic inCurTopic,File[] inFiles) {
    	 for (File file : inFiles) {
             if (file.isDirectory()) {
             	//if a directory, create a new topic and add files as children nodes
                 System.out.println("Directory: " + file.getName());
                 ITopic newParentTopic = inCurTopic.getOwnedWorkbook().createTopic();  //workbook.createTopic();                
                 newParentTopic.setTitleText(file.getName());
                 addTopics(newParentTopic,file.listFiles()); // Calls same method again.
                 inCurTopic.add(newParentTopic);
             } else {
                 System.out.println("Adding child node for File: " + file.getName());
                 ITopic topic = inCurTopic.getOwnedWorkbook().createTopic(); // workbook.createTopic();
             	topic.setTitleText(file.getName());
             	System.out.println("parent topic id:"+inCurTopic.getId());
             	//inCurTopic.getOwnedWorkbook().
             	inCurTopic.add(topic);
             	//inCurTopic.add(topic);
             }
         }
    }
    
    private String createRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) chars[rand.nextInt(chars.length)]);
        }
        return sb.toString();
    }

    public void testCompressionSpeed() throws Exception {
        
        createWorkbookContents(workbook);

        save(workbook, true, false);
        save(workbook, false, false);

        int sum1 = 0, sum2 = 0, iter = 100;
        for (int i = 0; i < iter; i++) {
            sum1 += save(workbook, true, false);
            sum2 += save(workbook, false, false);
        }
        System.out.println("Compressed (ms): " + (sum1 * 1.0 / iter));
        System.out.println("Uncompressed (ms): " + (sum2 * 1.0 / iter));
    }

    public void testCompressionSize() throws Exception {
        IWorkbook workbook = Core.getWorkbookBuilder().createWorkbook();
        createWorkbookContents(workbook);

        int size1 = save(workbook, true, true);
        int size2 = save(workbook, false, true);
        System.out.println("Compressed (bytes): " + size1);
        System.out.println("Uncompressed (bytes): " + size2);
    }

    private int save(IWorkbook workbook, boolean compressed,
        boolean returnSizeOrTime) throws IOException, CoreException {
        long start;
        long end;
        ByteArrayOutputStream stream = new ByteArrayOutputStream(524288);
        //FileOutputStream outFile;
        // NEW
        FileOutputStream fos = new FileOutputStream("hello-world.xmind");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        //ZipOutputStream zos = new ZipOutputStream(bos);
        
        // END NEW
        try {
            start = System.currentTimeMillis();
            workbook.save(new ZipStreamOutputTarget(
                    new ZipOutputStream(bos), compressed));
            end = System.currentTimeMillis();
            if (returnSizeOrTime)
                return stream.size();
            return (int) (end - start);
        } finally {
            stream.close();
        }
    }

    public void run() {
        try {
            testCompressionSize();
            //testCompressionSpeed();

            System.out.println("Done.");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CompressionTest().run();
    }

}
