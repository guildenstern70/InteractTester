/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.gui;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import com.jgoodies.looks.plastic.theme.Silver;
import com.jgoodies.looks.plastic.theme.SkyKrupp;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

/**
 *
 *
 */
public class Launcher
{
    public static void main(String[] args)
    {
        try
        {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    MainForm mf = MainForm.show();
                }
            });

        }
        catch (Exception exc)
        {
            System.err.println("Error: " + exc.getMessage());
            exc.printStackTrace();
        }

    }

    public static void setLookAndFeel(AxsLookAndFeel n, JFrame frame)
    {
        String laf = "";

        switch (n)
        {
            case XPLATFORM:
                laf = UIManager.getCrossPlatformLookAndFeelClassName();
                break;
            case SYSTEM:
                laf = UIManager.getSystemLookAndFeelClassName();
                break;
            case WINDOWS:
                laf = "com.jgoodies.looks.windows.WindowsLookAndFeel";
                break;
            case METAL:
                laf = "javax.swing.plaf.metal.MetalLookAndFeel";
                MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                break;
            case OCEAN:
                laf = "javax.swing.plaf.metal.MetalLookAndFeel";
                try
                {
                    MetalLookAndFeel.class.getMethod("getCurrentTheme", (Class[]) null);
                    MetalLookAndFeel.setCurrentTheme((MetalTheme)
                            Class.forName("javax.swing.plaf.metal.OceanTheme").newInstance());
                }
                catch (Exception e)
                {
                    MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                }
                break;
            case PLASTIC:
                PlasticLookAndFeel.setTabStyle(PlasticLookAndFeel.TAB_STYLE_METAL_VALUE);
                PlasticLookAndFeel.setPlasticTheme(new SkyKrupp());
                laf = "com.jgoodies.looks.plastic.PlasticLookAndFeel";
                break;
            case PLASTICXP:
                PlasticLookAndFeel.setTabStyle(PlasticLookAndFeel.TAB_STYLE_METAL_VALUE);
                PlasticLookAndFeel.setPlasticTheme(new ExperienceBlue());
                laf = "com.jgoodies.looks.plastic.PlasticXPLookAndFeel";
                break;
            case LOOKS:
                PlasticLookAndFeel.setTabStyle(PlasticLookAndFeel.TAB_STYLE_DEFAULT_VALUE);
                PlasticLookAndFeel.setPlasticTheme(new Silver());
                laf = "com.jgoodies.looks.plastic.Plastic3DLookAndFeel";
                break;
            default:
                laf = UIManager.getSystemLookAndFeelClassName();
                break;
            case NIMBUS:
                break;
        }

        try
        {
            UIManager.setLookAndFeel(laf);
            SwingUtilities.updateComponentTreeUI(frame);
            frame.validate();
        }
        catch (ClassNotFoundException cnfe)
        {
            System.err.println("Unknown Look And Feel: " + laf);
        }
        catch (UnsupportedLookAndFeelException ulafe)
        {
            System.err.println("Unsupported Look And Feel: " + laf);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
