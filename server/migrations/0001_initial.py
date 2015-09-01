# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('push_notifications', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='Projects',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('project_name', models.CharField(unique=True, max_length=30)),
                ('project_summary', models.CharField(max_length=500)),
                ('date_created', models.DateField(default=None, null=True, blank=True)),
                ('project_image', models.ImageField(default=None, null=True, upload_to=b'projects', blank=True)),
            ],
        ),
        migrations.CreateModel(
            name='Skills',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('skill_name', models.CharField(unique=True, max_length=30)),
                ('projects', models.ManyToManyField(related_name='skills', to='server.Projects')),
            ],
        ),
        migrations.CreateModel(
            name='Swipes',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('user_likes', models.CharField(default=None, max_length=3, null=True, blank=True, choices=[(b'YES', b'YES'), (b'NO', b'NO')])),
                ('project_likes', models.CharField(default=None, max_length=3, null=True, blank=True, choices=[(b'YES', b'YES'), (b'NO', b'NO')])),
                ('project', models.ForeignKey(to='server.Projects')),
            ],
        ),
        migrations.CreateModel(
            name='Types',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('type_name', models.CharField(unique=True, max_length=30)),
                ('projects', models.ManyToManyField(related_name='types', to='server.Projects')),
            ],
        ),
        migrations.CreateModel(
            name='UserProfiles',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('user_summary', models.CharField(default=None, max_length=500, null=True, blank=True)),
                ('location', models.CharField(default=None, max_length=30, null=True, blank=True)),
                ('user_image', models.ImageField(default=None, null=True, upload_to=b'users', blank=True)),
                ('device', models.ForeignKey(default=None, to='push_notifications.GCMDevice')),
                ('user', models.ForeignKey(to=settings.AUTH_USER_MODEL)),
            ],
        ),
        migrations.AddField(
            model_name='types',
            name='user_profiles',
            field=models.ManyToManyField(related_name='types', to='server.UserProfiles'),
        ),
        migrations.AddField(
            model_name='swipes',
            name='user_profile',
            field=models.ForeignKey(to='server.UserProfiles'),
        ),
        migrations.AddField(
            model_name='skills',
            name='user_profiles',
            field=models.ManyToManyField(related_name='skills', to='server.UserProfiles'),
        ),
        migrations.AddField(
            model_name='projects',
            name='owner',
            field=models.ForeignKey(to='server.UserProfiles'),
        ),
    ]
