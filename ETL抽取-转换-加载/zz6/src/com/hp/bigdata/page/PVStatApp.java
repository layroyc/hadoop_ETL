package com.hp.bigdata.page;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.hp.bigdata.utils.LogParser;

public class PVStatApp {
	public static void main(String[] args) throws Exception {
		//创建Configuration对象
				Configuration conf = new Configuration();
				//判断输出路径是否存在，存在删除
				FileSystem fs = FileSystem.get(conf);
				Path outputPath = new Path("out");
				if (fs.exists(outputPath)) {
						fs.delete(outputPath, true);
				}
				//创建job对象
				Job job = Job.getInstance(conf);
				//设置提交类
				job.setJarByClass(PVStatApp.class);
				//设置map相关信息
				job.setMapperClass(MyMapper.class);
				job.setMapOutputKeyClass(Text.class);
				job.setMapOutputValueClass(LongWritable.class);
				//设置reduce相关信息
				job.setReducerClass(MyReducer.class);
				job.setOutputKeyClass(NullWritable.class);
				job.setOutputValueClass(LongWritable.class);
				//设置输入输出路径
				FileInputFormat.setInputPaths(job, new Path("trackinfo_20130721.txt"));
				FileOutputFormat.setOutputPath(job, new Path("out"));
				//提交
				job.waitForCompletion(true);
	}
	static class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable>{
		private LogParser parser;
		private LongWritable ONE=new LongWritable(1);
		private Text KEY =new Text("KEY");
		@Override
		protected void setup(Mapper<LongWritable, Text, Text, LongWritable>.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			parser=new LogParser();
		}
		@Override
		protected void map(LongWritable key, Text value,
				org.apache.hadoop.mapreduce.Mapper.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			context.write(KEY, ONE);
		}
		}
	static class MyReducer extends Reducer<Text, LongWritable, NullWritable, LongWritable>{
		@Override
		protected void reduce(Text key, Iterable<LongWritable> values,
				Reducer<Text, LongWritable, NullWritable, LongWritable>.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			long sum = 0;
			for (LongWritable value : values) {
				sum++;
			}
			context.write(NullWritable.get(), new LongWritable(sum));
		}
	}

}
