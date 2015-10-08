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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;

import org.xmind.core.Core;
import org.xmind.core.CoreException;
import org.xmind.core.IBoundary;
import org.xmind.core.INotes;
import org.xmind.core.INotesContent;
import org.xmind.core.IPlainNotesContent;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;
import org.xmind.core.internal.Style;
import org.xmind.core.internal.zip.ZipStreamOutputTarget;
import org.xmind.core.style.IStyle;
import org.xmind.core.style.IStyleSheet;
import org.xmind.ui.mindmap.*;


@SuppressWarnings("nls")
public class CompressionTest {

    IWorkbook workbook = Core.getWorkbookBuilder().createWorkbook();

    IWorkbook curWorkbook;
   
    public CompressionTest() {
    	 try {
    		 File curBook = new File("Z:\\Users\\dhartman\\Dropbox\\hello-world_formatted.xmind");
    		 curWorkbook = Core.getWorkbookBuilder().loadFromFile(curBook);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void createWorkbookContents(IWorkbook workbook) {
        ITopic root = workbook.getPrimarySheet().getRootTopic();
        root.setTitleText("Code Repository");
        
       
        workbook.getPrimarySheet().getRootTopic().setStructureClass("org.xmind.ui.logic.right");
        //add a shell scripts node
        ITopic shellScripts = workbook.createTopic();
        shellScripts.setTitleText("Shell Scripts");
        
        ITopic perlScripts = workbook.createTopic();
        perlScripts.setTitleText("Perl Scripts");
        
        root.add(shellScripts);
        root.add(perlScripts);
        
        FilenameFilter textFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".sh")) {
					return true;
				} else {
					return false;
				}
			}
		};
        File[] files = new File("C:\\cygwin64\\home\\dhartman\\0-Common Objects\\").listFiles();  //  listFiles();
        //initialize curTopic to Root
       // System.out.println("root topic id:"+root.getId());
        addTopics(shellScripts,files);
    }

    private void addTopics(ITopic inCurTopic,File[] inFiles) {
    	 for (File file : inFiles) {
             if (file.isDirectory()) {
             	//if a directory, create a new topic and add files as children nodes
                 //System.out.println("Directory: " + file.getName());
                 ITopic newParentTopic = inCurTopic.getOwnedWorkbook().createTopic();  //workbook.createTopic();                
                 newParentTopic.setTitleText(file.getName()+"/");
                 addTopics(newParentTopic,file.listFiles()); // Calls same method again.
                 inCurTopic.add(newParentTopic);
             } else {
                
                if (file.getName().endsWith(".sh")) {
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
	                topic.getNotes().setContent(INotes.PLAIN, notes);
	              	inCurTopic.add(topic);
                }
             }
         }
    }
    private void getTopicInfo(ITopic inTopic) {
    	System.out.println("For topic: " + inTopic.getTitleText());
    	Set<IBoundary> bndry = inTopic.getBoundaries();
    	for (Iterator<IBoundary> bndryItr = bndry.iterator(); bndryItr.hasNext();){
    	      System.out.println("Boundry info: " + bndryItr.next().getStyleType());
    	}
    	 
    	IStyleSheet styleSheet = workbook.getStyleSheet();
    	System.out.println("Style sheet:" + styleSheet.toString());
    	Set<IStyle> allStyles = styleSheet.getAllStyles();
    	for (Iterator<IStyle> styleItr = allStyles.iterator(); styleItr.hasNext();){
  	      System.out.println("Style info: " + styleItr.next().getName());
  	    }
    	System.out.println("topic style id: " + inTopic.getStyleId());
    	System.out.println("topic style type id: " + inTopic.getStyleType());
    	
    	//IStyleSheet styleSheet = workbook.getStyleSheet();
    	IStyle style = styleSheet.findStyle(inTopic.getStyleId());
    	if (style == null) {
    		System.out.println("No style sheet");
    	  style = styleSheet.createStyle(IStyle.TOPIC);
    	  styleSheet.addStyle(style, IStyleSheet.NORMAL_STYLES);
    	  style.setProperty(Style. "0x000000");
    	  
    	  style.setProperty(Style.FillColor, “0x000000”);  // fill color black
    	  style.setProperty(Style.Textcolor, “0xffffff”);  // text color white
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
       // long start;
       // long end;
       // ByteArrayOutputStream stream = new ByteArrayOutputStream(524288);
        //FileOutputStream outFile;
        // NEW
        FileOutputStream fos = new FileOutputStream("Z:\\Users\\dhartman\\Dropbox\\hello-world.xmind");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        //ZipOutputStream zos = new ZipOutputStream(bos);
        
        // END NEW
        try {
            //start = System.currentTimeMillis();
            workbook.save(new ZipStreamOutputTarget(
                    new ZipOutputStream(bos),true));
            //end = System.currentTimeMillis();
        } finally {
            bos.close();
        }
    }
    int curLevel=0;
    public String indentString(int level) {
    	String retString = "";
    	for (int i=0;i<level*5;i++) {
    		retString= retString + " "; 
    	}
    	return retString;
    }
    private void removeChildless(List<ITopic> inTopics){
 //   private void removeChildless(IWorkbook inworkbook){
	// List<ITopic> allTopics = inworkbook.getPrimarySheet().getRootTopic().getAllChildren();
	 //List<Session> wfSessions = inWorkflow.getSessions();	
	 System.out.println(indentString(curLevel) + "At Level [" +curLevel + "] Topic has " + inTopics.size() +" nodes to review under node: " + inTopics.get(0).getParent().getTitleText());
		if (inTopics.size() != 0) {
			//loop through the children topics starting at this node...
			for (int i=0;i<inTopics.size();i++) {
				//System.out.println("Topic " + inTopics.get(i).getTitleText() +" has [" + inTopics.get(i).getAllChildren().size() + "] children");
				ITopic parentTopic = inTopics.get(i).getParent();
				if (inTopics.get(i).getAllChildren().size() != 0) {
					//for each of this ones children...call it again
					curLevel++;
					removeChildless(inTopics.get(i).getAllChildren());
					System.out.println(indentString(curLevel)+ "At level " + curLevel + " unwinding from loop for " + inTopics.get(i).getTitleText());
					if (!inTopics.get(i).hasChildren(ITopic.ATTACHED)) {
						System.out.println("Removing node as it has no children...and it did at some point " + inTopics.get(i).getTitleText());
						inTopics.get(i).getParent().remove(inTopics.get(i));
					}
					curLevel--;
				} else {
					if (inTopics.get(i).getTitleText().endsWith(".sh")) {
						//leave it alone
					} else {
						//remove it
						//removing this node (and it's children...I think)
						System.out.println(indentString(curLevel) + "Removed topic "+inTopics.get(i).getTitleText()+" from node: " + parentTopic.getTitleText());
						//parentTopic.setTitleText("DELETED:" + parentTopic.getTitleText());
						parentTopic.remove(inTopics.get(i));
						//double check to see if the partent is now childless...if so that node should be removed as well.
						//System.out.println("Now parent has children:" + parentTopic.getAllChildren().size());
						if (!parentTopic.hasChildren(ITopic.ATTACHED)) {
							//if the one removed was the last one and there are no other valid children...remove the parent node as well
							//System.out.println(indentString(curLevel-4) + "Removing parent node " + parentTopic.getTitleText() + " as well since no more children.");
							// get grand parent
							//ITopic grandParent = parentTopic.getParent();
							//System.out.println("grand parent is:" + grandParent.getTitleText() + " and has [" + grandParent.getAllChildren().size() + "] children");
							//parentTopic.getParent().remove(parentTopic);
							//System.out.println("grand parent is:" + grandParent.getTitleText() + " and has [" + grandParent.getAllChildren().size() + "] children");
							/*if (grandParent.hasChildren(ITopic.ATTACHED)) {
								System.out.println("grand has children" + grandParent.getTitleText());
								//if the parent still has children, try to remove them again
								removeChildless(grandParent.getAllChildren());
							}
							*/
						}
					//	inworkbook.getPrimarySheet().getRootTopic().remove(allTopics.get(i));
					}
				}
				//allTopics.get(i).getAllChildren();  //
			}
		}
}
int printIndentSize=-1;

public void printTree(ITopic inTopic) {
	if (inTopic.hasChildren(ITopic.ATTACHED)) {
		printIndentSize++;
		List<ITopic> childTopics = inTopic.getAllChildren();
		for (int i=0;i<childTopics.size();i++) {
			System.out.println(indentString(printIndentSize)+"Level [" + printIndentSize + "] node:" + childTopics.get(i).getTitleText());
			printTree(childTopics.get(i));
		}
		printIndentSize--;
	}
	
    	
}
public void run() {
        try {
          List<ITopic> level1Topics=curWorkbook.getPrimarySheet().getRootTopic().getAllChildren();
          for (int i=0;i<level1Topics.size();i++) {
        	  System.out.println("Getting Syle Sheet from node: " + level1Topics.get(i).getTitleText());
        	  getTopicInfo(level1Topics.get(i));
          }
      //    createWorkbookContents(workbook);
          //print out tree
     //     printTree(workbook.getPrimarySheet().getRootTopic());
      //    removeChildless(workbook.getPrimarySheet().getRootTopic().getAllChildren());
      //    save(workbook);
            System.out.println("Done.");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CompressionTest().run();
        
    }

}
