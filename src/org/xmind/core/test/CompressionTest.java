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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.xmind.core.Core;
import org.xmind.core.CoreException;
import org.xmind.core.IBoundary;
import org.xmind.core.INotes;
import org.xmind.core.INotesContent;
import org.xmind.core.IPlainNotesContent;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;
import org.xmind.core.internal.zip.ZipStreamOutputTarget;
import org.xmind.core.marker.IMarker;
import org.xmind.core.marker.IMarkerGroup;
import org.xmind.core.marker.IMarkerSheet;
import org.xmind.core.style.IStyle;
import org.xmind.core.style.IStyleSheet;
import org.xmind.core.util.FileUtils;
import org.xmind.ui.style.Styles;
import org.apache.commons.io.filefilter.*;


@SuppressWarnings("nls")
public class CompressionTest {


private static final String outFileName = "Z:\\Users\\dhartman\\Dropbox\\scripts.xmind";
public static final String startDirectory="C:\\cygwin64\\home\\dhartman\\";

public static final String[] perlExtensions = new String[] { "pl", "pm" };
public static final String[] shellExtensions = new String[] { "sh" };

IWorkbook workbook;
//IWorkbook curWorkbook;

//constructer
public CompressionTest() {

	System.out.println("Creating workbook: " + outFileName);
	workbook = Core.getWorkbookBuilder().createWorkbook();
	createStyles();

}
public ITopic shellScripts;
public ITopic perlScripts;
public static IStyleSheet styleSheet;
public static IStyle groupStyle ;
public static IStyle dirStyle ;
public static IStyle mapStyle;
public static IStyle fileStyle;
public static IMarker folderMarker ;
int curLevel=0;
int printIndentSize=-1;

private void createWorkbookContents(IWorkbook workbook) {
        ITopic root = workbook.getPrimarySheet().getRootTopic();
        workbook.getPrimarySheet().setStyleId(mapStyle.getId());

        root.setTitleText("Code Repository");
        Date dt = new Date();
        root.addLabel(dt.toString());
        workbook.getPrimarySheet().getRootTopic().setStructureClass("org.xmind.ui.logic.right");
        //add a shell scripts node
        shellScripts = workbook.createTopic();
        shellScripts.setTitleText("Shell Scripts");
        shellScripts.setStyleId(groupStyle.getId());
        
        perlScripts = workbook.createTopic();
        perlScripts.setTitleText("Perl Scripts");
        perlScripts.setStyleId(groupStyle.getId());
        
        root.add(shellScripts);
        root.add(perlScripts);
        
       
        
        File[] shFiles = getFileList(startDirectory,shellExtensions);
        //File[] shFiles = new File(startDirectory).listFiles();
        addTopics(shellScripts,shFiles,shellExtensions);
        
        File[] perlFiles = getFileList(startDirectory,perlExtensions);
        addTopics(perlScripts,perlFiles,perlExtensions);
        
        removeChildless(shellScripts.getAllChildren(),shellExtensions);
        removeChildless(perlScripts.getAllChildren(),perlExtensions);
        
        shellScripts.setFolded(true);
        perlScripts.setFolded(true);
    }

public File[] getFileList(String inPath,String[] fileExtensions) {
	//List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
	
	List<File> listFiles = (List<File>) org.apache.commons.io.FileUtils.listFilesAndDirs(
			new File(inPath), 
			new SuffixFileFilter(fileExtensions),
			//fileExtensions, 
			null);
			//TrueFileFilter.INSTANCE);
	File[] returnFileList = new File[listFiles.size()];
    listFiles.toArray(returnFileList);	
	return returnFileList;
}

private void addTopics(ITopic inCurTopic,File[] inFiles, String[] fileExtension) {
	 for (File file : inFiles) {
		// System.out.println("Looking at file: " + file.getName());
		 if(file.getName().startsWith(".")) {
			 //ignore any "." files or directories
			 continue;
		 }
         if (file.isDirectory()) {
         	//if a directory, create a new topic and add files as children nodes
             //System.out.println("Directory: " + file.getName());
             ITopic newParentTopic = inCurTopic.getOwnedWorkbook().createTopic();  //workbook.createTopic();                
             newParentTopic.setTitleText(file.getName()+"/");
             addTopics(newParentTopic,
            		// getFileList(file.getName(),fileExtension),
            		 file.listFiles(),
            		 fileExtension); // Calls same method again.
             newParentTopic.setStyleId(dirStyle.getId());
            // newParentTopic.addMarker(folderMarker.getId());
             inCurTopic.add(newParentTopic);
         } else {
        	  for (int i = 0; i < fileExtension.length; i++) {
				if (file.getName().endsWith(fileExtension[i])) {
					// if (file.getName().endsWith(fileExtension)) {
					//	System.out.println("Adding child node for File: " + file.getName());
					String fileText = null;
					try {
						fileText = readFile(file.getAbsolutePath());
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					ITopic topic = inCurTopic.getOwnedWorkbook().createTopic(); // workbook.createTopic();
					topic.setTitleText(file.getName());
					INotesContent notes = inCurTopic.getOwnedWorkbook().createNotesContent(INotes.PLAIN);
					((IPlainNotesContent) notes)
					.setTextContent(fileText);
					Date lastModified = new Date(file.lastModified()); 
					SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");  
					String formattedDateString = formatter.format(lastModified); 
					List<String> dateCollection = new ArrayList<String>();
					dateCollection.add(formattedDateString);
					//topic.setLabels(dateCollection);
					topic.getNotes().setContent(INotes.PLAIN, notes);
					topic.setStyleId(fileStyle.getId());
					inCurTopic.add(topic);
				}
			}  //end if file has right extension
         }  //end else
     }
}
  
public void createStyles() {
	//System.out.println("Setting up the style sheets");
	 styleSheet = workbook.getStyleSheet();
	 //workbook.getPrimarySheet().
	 
	 mapStyle = styleSheet.createStyle(IStyle.MAP);
	 mapStyle.setProperty(Styles.BackgroundColor, "#999999");
	 styleSheet.addStyle(mapStyle, IStyleSheet.NORMAL_STYLES);
	 
	 groupStyle = styleSheet.createStyle(IStyle.TOPIC);
	 groupStyle.setProperty(Styles.FillColor,"#FF7F00");
	 groupStyle.setProperty(Styles.TextColor,"#ffffff");
	 groupStyle.setProperty(Styles.FontWeight,Styles.FONT_WEIGHT_BOLD);
	 groupStyle.setProperty(Styles.LineClass, Styles.BRANCH_CONN_ROUNDEDELBOW);
     styleSheet.addStyle(groupStyle, IStyleSheet.NORMAL_STYLES);
  
     dirStyle = styleSheet.createStyle(IStyle.TOPIC);
     dirStyle.setProperty(Styles.FillColor,"#0083BF");
     dirStyle.setProperty(Styles.TextColor,"#ffffff");
     dirStyle.setProperty(Styles.FontWeight,Styles.FONT_WEIGHT_BOLD);
     dirStyle.setProperty(Styles.LineClass, Styles.BRANCH_CONN_ROUNDEDELBOW);
     styleSheet.addStyle(dirStyle, IStyleSheet.NORMAL_STYLES);
     
     fileStyle = styleSheet.createStyle(IStyle.TOPIC);
     fileStyle.setProperty(Styles.FillColor,"#FF7F00");
     fileStyle.setProperty(Styles.TextColor,"#ffffff");
   //  fileStyle.setProperty(Styles.FontWeight,Styles.FONT_WEIGHT_BOLD);
     fileStyle.setProperty(Styles.LineClass, Styles.BRANCH_CONN_ROUNDEDELBOW);
     fileStyle.setProperty(Styles.FontSize, "10");
     fileStyle.setProperty(Styles.ShapeClass,Styles.TOPIC_SHAPE_ROUNDEDRECT);
    // fileStyle.setProperty(Styles.Label, "10");     
     styleSheet.addStyle(fileStyle, IStyleSheet.NORMAL_STYLES);
 	  
}
private void getTopicInfo(ITopic inTopic) {
	Set<IBoundary> bndry = inTopic.getBoundaries();
	for (Iterator<IBoundary> bndryItr = bndry.iterator(); bndryItr.hasNext();){
	      System.out.println("Boundry info: " + bndryItr.next().getStyleType());
	}
	 
	IStyleSheet styleSheet = workbook.getStyleSheet();
	Set<IStyle> allStyles = styleSheet.getAllStyles();
	
	IStyle style = styleSheet.findStyle(inTopic.getStyleId());
	if (style == null) {
	  style = styleSheet.createStyle(IStyle.TOPIC);
	  styleSheet.addStyle(style, IStyleSheet.NORMAL_STYLES);
	  style.setProperty(Styles.FillColor,"0x#0083BF");
	}
}
private String readFile( String file ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader (file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }

        return stringBuilder.toString();
    }
private void save(IWorkbook workbook) throws IOException, CoreException {
  
    FileOutputStream fos = new FileOutputStream(outFileName);
    BufferedOutputStream bos = new BufferedOutputStream(fos);
    try {
       workbook.save(new ZipStreamOutputTarget(
       new ZipOutputStream(bos),true));
    } finally {
        bos.close();
        System.out.println("Saved workbook: " + outFileName);
    }
}

public String indentString(int level) {
	String retString = "";
	for (int i=0;i<level*5;i++) {
		retString= retString + " "; 
    	}
    	return retString;
    }
private void removeChildless(List<ITopic> inTopics, String[] fileExtension){

	if (inTopics.size() != 0) {
		//loop through the children topics starting at this node...
		for (int i=0;i<inTopics.size();i++) {
			ITopic parentTopic = inTopics.get(i).getParent();
			if (inTopics.get(i).getAllChildren().size() != 0) {
				curLevel++;
				removeChildless(inTopics.get(i).getAllChildren(),fileExtension);
				if (!inTopics.get(i).hasChildren(ITopic.ATTACHED)) {
					inTopics.get(i).getParent().remove(inTopics.get(i));
				}
				curLevel--;
			} else {
				boolean noExtensionMatch=false;
				for (int j = 0; j < fileExtension.length; j++) {
					if (inTopics.get(i).getTitleText().endsWith(fileExtension[j]) && !noExtensionMatch) {
						noExtensionMatch=true;
					} else {
						//remove parent?!?
					}
				} //after going through all extensions...see if we should remove or not
				if(!noExtensionMatch) {
					parentTopic.remove(inTopics.get(i));
				}
			}
		}
	}
}


public void printTree(ITopic inTopic) {
	if (inTopic.hasChildren(ITopic.ATTACHED)) {
		printIndentSize++;
		List<ITopic> childTopics = inTopic.getAllChildren();
		for (int i=0;i<childTopics.size();i++) {
//			System.out.println(indentString(printIndentSize)+"Level [" + printIndentSize + "] node:" + childTopics.get(i).getTitleText());
			printTree(childTopics.get(i));
		}
		printIndentSize--;
	}
	
    	
}
public void run() {
        try {
//          List<ITopic> level1Topics=curWorkbook.getPrimarySheet().getRootTopic().getAllChildren();
//          for (int i=0;i<level1Topics.size();i++) {
//        	  System.out.println("Getting Syle Sheet from node: " + level1Topics.get(i).getTitleText());
//        	  getTopicInfo(level1Topics.get(i));
//          }
          createWorkbookContents(workbook);
          save(workbook);
          System.out.println("Done.");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CompressionTest().run();
        
    }

}
