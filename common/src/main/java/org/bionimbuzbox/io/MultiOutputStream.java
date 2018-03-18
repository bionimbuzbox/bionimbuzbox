package org.bionimbuzbox.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MultiOutputStream extends OutputStream {

	private List<OutputStream> out;

	public MultiOutputStream(List<OutputStream> outStreams) {

		this.out = new LinkedList<OutputStream>();

		for (Iterator<OutputStream> i = outStreams.iterator(); i.hasNext();) {
			OutputStream outputStream = i.next();

			if(outputStream == null){
				throw new NullPointerException();
			}
			this.out.add(outputStream);
		}
	}
	
	public MultiOutputStream(OutputStream... outStreams) {

		this.out = new LinkedList<OutputStream>();
		
		for (int i = 0; i < outStreams.length; i++) {
			OutputStream outputStream = (OutputStream) outStreams[i];

			if(outputStream == null){
				throw new NullPointerException();
			}
			this.out.add(outputStream);
		}
	}

	@Override
	public void write(int arg0) throws IOException {

		for (Iterator<OutputStream> i = out.iterator(); i.hasNext();) {
			OutputStream var = (OutputStream) i.next();

			var.write(arg0);
		}
	}

	@Override
	public void write(byte[] b) throws IOException{

		for (Iterator<OutputStream> i = out.iterator(); i.hasNext();) {
			OutputStream var = (OutputStream) i.next();

			var.write(b);
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException{

		for (Iterator<OutputStream> i = out.iterator(); i.hasNext();) {
			OutputStream var = (OutputStream) i.next();

			var.write(b, off, len);
		}
	}

	@Override
	public void close() throws IOException{

		for (Iterator<OutputStream> i = out.iterator(); i.hasNext();) {
			OutputStream var = (OutputStream) i.next();

			var.close();
		}
	}

	@Override
	public void flush() throws IOException{

		for (Iterator<OutputStream> i = out.iterator(); i.hasNext();) {
			OutputStream var = (OutputStream) i.next();

			var.flush();
		}
	}

}

