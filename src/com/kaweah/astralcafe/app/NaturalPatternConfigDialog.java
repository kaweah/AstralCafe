package com.kaweah.astralcafe.app;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.kaweah.astralcafe.starbinder.SimpleBinder;

@SuppressWarnings("serial")
public class NaturalPatternConfigDialog extends JDialog implements ActionListener
{
	private JTextField lowerMaxEditField = new JTextField();
	private JTextField upperMaxEditField = new JTextField();
	private JTextField maxLinksEditField = new JTextField();

	public NaturalPatternConfigDialog(JFrame owner)
	{
		super(owner, "Edit Natural Pattern Config", true);
		
		Container contentPane = getContentPane();
				
		JPanel settingsPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		contentPane.add(settingsPanel, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);

		settingsPanel.setLayout(new GridBagLayout());		
		GridBagConstraints cc = new GridBagConstraints();
		
		JLabel lowerMaxLabel = new JLabel("Lower Max Distance");
		JLabel upperMaxLabel = new JLabel("Upper Max Distance");
		JLabel maxLinksLabel = new JLabel("Max Links Per Star");
				
		lowerMaxEditField.setText(" " + SimpleBinder.lowerMaxDistance);
		upperMaxEditField.setText(" " + SimpleBinder.upperMaxDistance);
		maxLinksEditField.setText(" " + SimpleBinder.maxLinksPerStar);
		
		cc.gridx = 1;
		cc.gridy = 1;
		
		settingsPanel.add(lowerMaxLabel, cc);
		cc.gridy = 2;
		settingsPanel.add(upperMaxLabel, cc);
		cc.gridy = 3;
		settingsPanel.add(maxLinksLabel, cc);
		cc.gridx = 2;
		cc.gridy = 1;
		settingsPanel.add(new JLabel(" "), cc);
		cc.gridx = 3;
		settingsPanel.add(lowerMaxEditField, cc);
		cc.gridy = 2;
		settingsPanel.add(upperMaxEditField, cc);
		cc.gridy = 3;
		settingsPanel.add(maxLinksEditField, cc);
		
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");
		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		int width = 300;
		int height = 250;
		int x = owner.getX()+(owner.getWidth()-width) / 2;
		int y = owner.getY()+(owner.getHeight()-height) / 2;
		setBounds(x, y, width, height);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		
		if ("OK".equals(command))
		{
			int lowerMaxDistance = Integer.valueOf(lowerMaxEditField.getText().trim());
			int upperMaxDistance = Integer.valueOf(upperMaxEditField.getText().trim());
			int maxLinksPerStar  = Integer.valueOf(maxLinksEditField.getText().trim());
			
			if (lowerMaxDistance != SimpleBinder.lowerMaxDistance ||
				upperMaxDistance != SimpleBinder.upperMaxDistance ||
				maxLinksPerStar  != SimpleBinder.maxLinksPerStar)
			{
				SimpleBinder.lowerMaxDistance = lowerMaxDistance;
				SimpleBinder.upperMaxDistance = upperMaxDistance;
				SimpleBinder.maxLinksPerStar  = maxLinksPerStar;
				// canvas needs to be refreshed/repainted
			}
		}
		
		setVisible(false);
	}
}
